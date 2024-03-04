package com.example.springboot.common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 接口统一返回包装类
*@return
*/


@Data
//无参构造
@NoArgsConstructor
//有参构造
@AllArgsConstructor
public class Result {
    private String code; //返回结果的响应码
    private String msg; //后端告诉前台请求失败的原因
    private Object data;

    public static Result success(){
        return new Result(Constants.CODE_200,"",null);
    }

    public static Result success(Object data){
        return new Result(Constants.CODE_200,"",data);
    }

    public static Result error(String code,String msg){
        return new Result(code,msg,null);
    }

    public static Result error(){
        return new Result(Constants.CODE_500,"系统错误",null);
    }
}
