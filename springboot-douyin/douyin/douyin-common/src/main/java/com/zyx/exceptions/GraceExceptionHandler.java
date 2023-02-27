package com.zyx.exceptions;

import com.zyx.grace.result.GraceJSONResult;
import com.zyx.grace.result.ResponseStatusEnum;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 统一异常拦截处理
 * 可以针对异常的类型进行捕获，然后返回json信息到前端
 */
@ControllerAdvice
public class GraceExceptionHandler {

    //捕获MyCustomException异常
    @ExceptionHandler(MyCustomException.class)
    //返回json信息到前端
    @ResponseBody
    public GraceJSONResult returnMyException(MyCustomException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

    //捕获异常  //方法的参数无法被校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //返回json信息到前端
    @ResponseBody
    public GraceJSONResult returnMethodNotValid(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> errors = getErrors(result);
        return GraceJSONResult.errorMap(errors);
    }

    //捕获异常  //文件大小上传异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public GraceJSONResult returnMaxUploadSize(MaxUploadSizeExceededException e) {
//        e.printStackTrace();
        return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_2MB_ERROR);
    }

    //获取字段相应的错误信息
    public Map<String,String> getErrors(BindingResult result) {

        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError fe : errorList){
            //获得错误字段所对应的属性（字段）名
            String field = fe.getField();
            //错误信息
            String msg = fe.getDefaultMessage();
            map.put(field,msg);
        }
        return map;
    }

}
