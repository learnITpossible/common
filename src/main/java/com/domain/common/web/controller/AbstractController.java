package com.domain.common.web.controller;

import com.domain.common.web.annotation.HtmlXssFilter;
import com.domain.common.web.annotation.NotHtmlXssFilter;
import com.domain.common.web.controller.validators.ControllerValidatorError;
import com.domain.common.web.exception.WebControllerException;
import com.domain.common.web.exception.WebServiceException;
import com.domain.common.web.exception.ControllerValidatorException;
import com.domain.common.web.response.BaseRet;
import com.domain.common.web.response.Response;
import com.domain.common.web.response.ResponseUtils;
import com.domain.common.utils.XssFilterUtil;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;

public abstract class AbstractController {

    protected final static String AUTO_PAGE = "auto";

    protected final static String REQUEST_URL = "requestUrl";

    protected final static String REQUEST_ACTION = "requestAction";

    protected static final String REQUEST_ACTION_CATALOG = "requestActionCatalog";

    private static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    protected String basePath;

    @Autowired
    protected Validator validator;

    protected String getBasePath() {

        if (basePath == null) {
            RequestMapping requestMappingAnno = AnnotationUtils.findAnnotation(this.getClass(), RequestMapping.class);
            if (requestMappingAnno != null) {
                if (requestMappingAnno.value().length > 0) {
                    basePath = requestMappingAnno.value()[0];
                }
            }
            if (basePath == null) {
                basePath = "";
            }
        }
        return basePath;
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    @ResponseBody
    public Object exceptionHandler(Throwable e, HttpServletRequest request, HttpServletResponse response) {

        String XRequestedWith = request.getHeader("X-Requested-With");
        if (XRequestedWith != null && XRequestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            return exceptionHandlerResponseBody(e, request, response);
        } else {
            Response respData = handleExceptionResponse(e, request, response);
            try {
                String contextPath = request.getContextPath();
                StringBuffer errUrl = new StringBuffer(contextPath);
                errUrl.append("/error.html?code=")
                        .append(respData.ret.getCode())
                        .append("&errMsg=");
                String msg = respData.ret.getMsg();

                if (msg == null)
                    msg = e.getMessage();
                if (msg != null)
                    errUrl.append(URLEncoder.encode(msg, "utf-8"));
                response.sendRedirect(errUrl.toString());
            } catch (IOException e1) {
                logger.error(e1.getMessage(), e1);
            }
            return null;
        }
    }

    protected Object exceptionHandlerResponseBody(Throwable e, HttpServletRequest request, HttpServletResponse response) {

        Response respData = handleExceptionResponse(e, request, response);
        return respData;
    }

    protected String exceptionHandlerPage(Throwable e, HttpServletRequest request, HttpServletResponse response) {

        Response respData = handleExceptionResponse(e, request, response);
        String msg = respData.ret.getMsg();

        if (msg == null)
            msg = e.getMessage();

        request.setAttribute("code", respData.ret.getCode());
        request.setAttribute("errMsg", msg);
        return "error";
    }

    protected Response handleExceptionResponse(Throwable e, HttpServletRequest request, HttpServletResponse response) {

        if (logger.isDebugEnabled()) {
            logger.debug(e.getMessage(), e);
        } else {
            // logger.error(e.getClass().getSimpleName() + ":" + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        Throwable causeE = e.getCause();
        if (causeE != null) {
            while (causeE.getCause() != null) {
                causeE = causeE.getCause();
            }
            e = causeE;
        }

        Response respData = null;
        if (e instanceof ControllerValidatorException) {
            ControllerValidatorException targetE = (ControllerValidatorException) e;
            respData = ResponseUtils.instance(BaseRet.ERROR_VALIDATOR_FIELDS.code, BaseRet.ERROR_VALIDATOR_FIELDS.msg, null, targetE.getErrors());
        } else if (e instanceof WebControllerException) {
            WebControllerException targetE = (WebControllerException) e;
            BaseRet ret = targetE.getRet();
            respData = ResponseUtils.instance(ret);
        } else if (e instanceof WebServiceException) {
            WebServiceException targetE = (WebServiceException) e;
            BaseRet ret = targetE.getRet();
            respData = ResponseUtils.instance(ret);
        } else {
            String msg = e.getMessage() != null ? e.getMessage() : BaseRet.EXCEPTION_ERROR.msg;
            respData = ResponseUtils.instance(BaseRet.EXCEPTION_ERROR.code, msg, null);
        }
        return respData;
    }

    protected Object invokeMenthod(String action, ModelMap modelMap,
            HttpServletRequest request, HttpServletResponse response, int actionType) throws Exception {

        Method[] methods = this.getClass().getMethods();
        Method invokeMethod = null;
        String suffix = "";
        if (actionType == 1) {
            suffix = "Page";
        } else if (actionType == 2) {
            suffix = "Data";
        }
        for (Method method : methods) {
            if (method.getName().equals(action + suffix)) {
                invokeMethod = method;
                break;
            }
        }
        if (invokeMethod == null) {
            return null;
        }
        ErrorsDelegate errorsDelegate = new ErrorsDelegate();
        Object[] args = resolveHandlerArguments(invokeMethod, this, request, response, modelMap, errorsDelegate);
        if (errorsDelegate.errors != null && errorsDelegate.errors.hasErrors()) {
            Errors errors = errorsDelegate.errors;
            ControllerValidatorException errorsException = new ControllerValidatorException("validator errors: " + errors);
            for (FieldError errorField : errors.getFieldErrors()) {
                ControllerValidatorError error = new ControllerValidatorError();
                error.setName(errorField.getField());
                error.setMsg(errorField.getDefaultMessage());
                errorsException.addError(error);
            }
            throw errorsException;
        }
        Object result = invokeMethod.invoke(this, args);
        return result;

    }

    @RequestMapping(value = "/data_{action}.html")
    @ResponseBody
    public Object invokeMenthodResponseBody(
            @PathVariable("action") String action,
            ModelMap map,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            Object result = invokeMenthod(action, map, request, response, 2);
            return result;
        } catch (Exception e) {
            Throwable targetE = e;
            if (e instanceof InvocationTargetException) {
                targetE = ((InvocationTargetException) e).getTargetException();
            }
            return exceptionHandlerResponseBody(targetE, request, response);
        }
    }

    @RequestMapping(value = "/{action}.html")
    public String invokeMenthodPage(
            @PathVariable("action") String action,
            ModelMap modelMap,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {

            String requestActionCatalog = request.getRequestURI();
            requestActionCatalog = requestActionCatalog.substring(0, requestActionCatalog.lastIndexOf("/"));
            requestActionCatalog = requestActionCatalog.substring(requestActionCatalog.lastIndexOf("/") + 1);

            request.setAttribute(REQUEST_URL, request.getServletPath());
            request.setAttribute(REQUEST_ACTION, action);
            request.setAttribute(REQUEST_ACTION_CATALOG, requestActionCatalog);

            Object result = invokeMenthod(action, modelMap, request, response, 1);

            String page = null;
            if (AUTO_PAGE.equals(result) || result == null) {
                StringBuffer url = new StringBuffer(getBasePath());
                url.append("/").append(action);
                page = url.toString();
            } else {
                page = (String) result;
            }
            return page;
        } catch (Exception e) {
            Throwable targetE = e;
            if (e instanceof InvocationTargetException) {
                targetE = ((InvocationTargetException) e).getTargetException();
            }
            return exceptionHandlerPage(targetE, request, response);
        }

    }

    ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    SimpleTypeConverter typeConverter = new SimpleTypeConverter();

    private Object[] resolveHandlerArguments(Method handlerMethod, Object handler,
                                             HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, ErrorsDelegate errors) throws Exception {

        Class[] paramTypes = handlerMethod.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < args.length; i++) {
            MethodParameter methodParam = new MethodParameter(handlerMethod, i);
            methodParam.initParameterNameDiscovery(this.parameterNameDiscoverer);
            GenericTypeResolver.resolveParameterType(methodParam, handler.getClass());
            String paramName = null;
            String attrName = null;
            boolean required = false;
            String defaultValue = null;
            int annotationsFound = 0;
            boolean htmlXssFilter = false;
            boolean notHtmlXssFilter = false;
            Annotation[] paramAnns = methodParam.getParameterAnnotations();

            for (Annotation paramAnn : paramAnns) {
                if (RequestParam.class.isInstance(paramAnn)) {
                    RequestParam requestParam = (RequestParam) paramAnn;
                    paramName = requestParam.value();
                    required = requestParam.required();
                    defaultValue = parseDefaultValueAttribute(requestParam.defaultValue());
                    annotationsFound++;
                } else if (ModelAttribute.class.isInstance(paramAnn)) {
                    ModelAttribute attr = (ModelAttribute) paramAnn;
                    attrName = attr.value();
                    annotationsFound++;
                } else if (HtmlXssFilter.class.isInstance(paramAnn)) {
                    htmlXssFilter = true;
                } else if (NotHtmlXssFilter.class.isInstance(paramAnn)) {
                    notHtmlXssFilter = true;
                }
            }
            if (annotationsFound == 0) {
                args[i] = resolveStandardArgument(methodParam.getParameterType(), request, response, modelMap);
            } else if (annotationsFound > 1) {
                throw new IllegalStateException("Handler parameter annotations are exclusive choices - " +
                        "do not specify more than one such annotation on the same parameter: " + handlerMethod);
            }

            if (paramName != null) {
                args[i] = resolveRequestParam(paramName, required, defaultValue, methodParam, request);
                if (args[i] instanceof String) {
                    if (htmlXssFilter) {
                        args[i] = XssFilterUtil.filter2Html((String) args[i]);
                    } else if (notHtmlXssFilter) {
                        // 不过滤
                    } else {
                        args[i] = XssFilterUtil.filter2Text((String) args[i]);
                    }
                }
            } else if (attrName != null) {
                args[i] = resolveModelAttribute(attrName, methodParam, request, response, handler, errors);
                filterModelAttributeHtmlXss(args[i]);
            }
        }

        return args;
    }

    /**
     * 过滤xss注入
     * @param obj
     * @throws Exception
     */
    private void filterModelAttributeHtmlXss(Object obj) throws Exception {

        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(obj.getClass());
        for (PropertyDescriptor pd : propertyDescriptors) {
            String name = pd.getName();
            if (name.equals("class")) {
                continue;
            }
            Field field = FieldUtils.getDeclaredField(obj.getClass(), name, true);
            if (field == null) {
                continue;
            }
            Class<?> propertyType = pd.getPropertyType();
            if (propertyType.isAssignableFrom(String.class)) {
                String value = (String) pd.getReadMethod().invoke(obj);
                if (value == null) continue;
                if (field.isAnnotationPresent(HtmlXssFilter.class)) {
                    value = XssFilterUtil.filter2Html(value);
                    pd.getWriteMethod().invoke(obj, value);
                } else if (field.isAnnotationPresent(NotHtmlXssFilter.class)) {
                    //不过滤
                } else {
                    value = XssFilterUtil.filter2Text(value);
                    pd.getWriteMethod().invoke(obj, value);
                }
            }
        }
    }

    private String getRequiredParameterName(MethodParameter methodParam) {

        String name = methodParam.getParameterName();
        if (name == null) {
            throw new IllegalStateException(
                    "No parameter name specified for argument of type [" + methodParam.getParameterType().getName() +
                            "], and no parameter name information found in class file either.");
        }
        return name;
    }

    private Object resolveRequestParam(String paramName, boolean required, String defaultValue,
                                       MethodParameter methodParam, HttpServletRequest webRequest)
            throws Exception {

        Class<?> paramType = methodParam.getParameterType();
        if (Map.class.isAssignableFrom(paramType) && paramName.length() == 0) {
            return resolveRequestParamMap((Class<? extends Map>) paramType, webRequest);
        }
        if (paramName.length() == 0) {
            paramName = getRequiredParameterName(methodParam);
        }
        Object paramValue = null;
        MultipartRequest multipartRequest = WebUtils.getNativeRequest(webRequest, MultipartRequest.class);
        if (multipartRequest != null) {
            List<MultipartFile> oriFiles = multipartRequest.getFiles(paramName);
            List<MultipartFile> files = new ArrayList<MultipartFile>();
            for (MultipartFile file : oriFiles) {
                if (file.getSize() > 0) {
                    files.add(file);
                }
            }
            if (!files.isEmpty()) {
                paramValue = (files.size() == 1 ? files.get(0) : files);
            }
        }
        if (paramValue == null) {
            String[] paramValues = webRequest.getParameterValues(paramName);
            if (paramValues != null) {
                paramValue = (paramValues.length == 1 ? paramValues[0] : paramValues);
            }
        }
        if (paramValue != null && !String.class.equals(paramType) && paramValue.equals("")) {
            paramValue = null;
        }
        if (paramValue == null) {
            if (defaultValue != null) {
                paramValue = defaultValue;
            } else if (required) {
                raiseMissingParameterException(paramName, paramType);
            }
            paramValue = checkValue(paramName, paramValue, paramType);
        }
        return typeConverter.convertIfNecessary(paramValue, paramType, methodParam);
    }

    private Map resolveRequestParamMap(Class<? extends Map> mapType, HttpServletRequest webRequest) {

        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        if (MultiValueMap.class.isAssignableFrom(mapType)) {
            MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(parameterMap.size());
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                for (String value : entry.getValue()) {
                    result.add(entry.getKey(), value);
                }
            }
            return result;
        } else {
            Map<String, String> result = new LinkedHashMap<String, String>(parameterMap.size());
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                if (entry.getValue().length > 0) {
                    result.put(entry.getKey(), entry.getValue()[0]);
                }
            }
            return result;
        }
    }

    private Object checkValue(String name, Object value, Class paramType) {

        if (value == null) {
            if (boolean.class.equals(paramType)) {
                return Boolean.FALSE;
            } else if (int.class.equals(paramType) || long.class.equals(paramType)
                    || short.class.equals(paramType)
                    || float.class.equals(paramType)
                    || double.class.equals(paramType)
                    || byte.class.equals(paramType)) {
                return 0;
            } else if (paramType.isPrimitive()) {
                throw new IllegalStateException("Optional " + paramType + " parameter '" + name +
                        "' is not present but cannot be translated into a null value due to being declared as a " +
                        "primitive type. Consider declaring it as object wrapper for the corresponding primitive type.");
            }
        }
        return value;
    }

    protected String parseDefaultValueAttribute(String value) {

        return (ValueConstants.DEFAULT_NONE.equals(value) ? null : value);
    }

    protected void raiseMissingParameterException(final String paramName, final Class paramType) throws Exception {

        throw new MissingServletRequestParameterException(paramName, paramType.getSimpleName()) {
            public String getMessage() {

                return "必选参数 '" + paramName + "' 不存在！";
            }
        };
    }

    protected Object resolveStandardArgument(Class<?> parameterType, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {

        if (ServletRequest.class.isAssignableFrom(parameterType) ||
                MultipartRequest.class.isAssignableFrom(parameterType)) {

            return request;
        } else if (ServletResponse.class.isAssignableFrom(parameterType)) {

            return response;
        } else if (HttpSession.class.isAssignableFrom(parameterType)) {
            return request.getSession();
        } else if (Principal.class.isAssignableFrom(parameterType)) {
            return request.getUserPrincipal();
        } else if (Locale.class.equals(parameterType)) {
            return RequestContextUtils.getLocale(request);
        } else if (InputStream.class.isAssignableFrom(parameterType)) {
            return request.getInputStream();
        } else if (Reader.class.isAssignableFrom(parameterType)) {
            return request.getReader();
        } else if (ModelMap.class.isAssignableFrom(parameterType)) {
            return modelMap;
        }
        return WebArgumentResolver.UNRESOLVED;
    }

    private Object resolveModelAttribute(String attrName, MethodParameter methodParam,
                                         HttpServletRequest request, HttpServletResponse response, Object handler, ErrorsDelegate errorsDelegate) throws Exception {

        String name = attrName;
        if ("".equals(name)) {
            name = Conventions.getVariableNameForParameter(methodParam);
        }
        Class<?> paramType = methodParam.getParameterType();
        Object bindObject = BeanUtils.instantiateClass(paramType);
        //		org.apache.commons.beanutils.BeanUtils.populate(bindObject, request.getParameterMap());
        applyPropertyValues(request, bindObject);
        Errors errors = new BeanPropertyBindingResult(
                bindObject, bindObject.getClass().getSimpleName());

        errorsDelegate.errors = errors;
        validator.validate(bindObject, errors);

        return bindObject;
    }

    protected void applyPropertyValues(HttpServletRequest request, Object bindObject) {

        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
        MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        }
        applyPropertyValues(mpvs, bindObject);
    }

    protected void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {

        for (Map.Entry<String, List<MultipartFile>> entry : multipartFiles.entrySet()) {
            String key = entry.getKey();
            List<MultipartFile> values = entry.getValue();
            if (values.size() == 1) {
                MultipartFile value = values.get(0);
                if (!value.isEmpty()) {
                    mpvs.add(key, value);
                }
            } else {
                mpvs.add(key, values);
            }
        }
    }

    protected void applyPropertyValues(MutablePropertyValues mpvs, Object bindObject) {

        try {
            // Bind request parameters onto target object.
            createDirectFieldAccessor(bindObject).setPropertyValues(mpvs, true, true);
        } catch (PropertyBatchUpdateException ex) {
            logger.error(ex.getMessage(), ex);
            // Use bind error processor to create FieldErrors.
            /*for (PropertyAccessException pae : ex.getPropertyAccessExceptions()) {
                getBindingErrorProcessor().processPropertyAccessException(pae, getInternalBindingResult());
            }*/
        }
    }

    /*
    public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
        // Create field error with the exceptions's code, e.g. "typeMismatch".
        String field = ex.getPropertyName();
        String[] codes = bindingResult.resolveMessageCodes(ex.getErrorCode(), field);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), field);
        Object rejectedValue = ex.getValue();
        if (rejectedValue != null && rejectedValue.getClass().isArray()) {
            rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
        }
        bindingResult.addError(new FieldError(
                bindingResult.getObjectName(), field, rejectedValue, true,
                codes, arguments, ex.getLocalizedMessage()));
    }*/
    protected ConfigurablePropertyAccessor createDirectFieldAccessor(Object target) {

        return PropertyAccessorFactory.forDirectFieldAccess(target);
    }

    public Validator getValidator() {

        return validator;
    }

    public void setValidator(Validator validator) {

        this.validator = validator;
    }

    private class ErrorsDelegate {

        Errors errors;
    }

}
