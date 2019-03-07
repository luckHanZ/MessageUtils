package com.zt.messagespringboot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @Title: ResultJsonMst.java
 * @Package lb.springboot.utils
 * @Description: 自定义响应数据结构 这个类是提供给门户，ios，安卓，微信商城用的
 *               门户接受此类数据后需要使用本类的方法转换成对于的数据类型格式（类，或者list） 其他自行处理 
 *               0：表示成功
 *               1：表示错误，错误信息在msg字段中 
 *               404：url访问路径错误
 *               405：请求方式不支持
 *               500：表示服务内部错误，错误信息在msg字段中
 *               501：bean验证错误，不管多少个错误都以map形式返回
 *               502：拦截器拦截到用户token出错 （这里待定） 
 *               555：异常抛出信息
 * @author Lyk
 */
public class ResultJson {
	private static Logger logger = LoggerFactory.getLogger(ResultJson.class);
	// 定义一个Jackson对象
	private static final ObjectMapper mapper = new ObjectMapper();
	// 相应业务状态
	private Integer status;
	// 响应消息
	private String msg;
	// 响应中的数据
	private Object data;

	// 默认无参构造
	public ResultJson() {

	}

	// 手动设置 返回的状态，提示的文言，数据内容
	public static ResultJson build(Integer status, String msg, Object data) {
		return new ResultJson(status, msg, data);
	}
	public static ResultJson buildSuc(Integer status, String msg, Object data) {
		return new ResultJson(status, msg, data);
	}

	// 手动设置 返回的状态，提示的文言
	public static ResultJson build(Integer status, String msg) {
		return new ResultJson(status,msg);
	}

	// 返回成功，提示文言交有前端处理
	public static ResultJson ok() {
		return new ResultJson(null);
	}

	// 返回失败，向页面返回错误信息提示
	public static ResultJson errorMsg(String msg) {
		return new ResultJson(500, msg, null);
	}

	// 返回失败，向页面返回错误信息提示,错误码需指定
	public static ResultJson errorCodeMsg(Integer status, String msg) {
		return new ResultJson(status, msg);
	}

	// 返回失败，向页面返回错误信息提示
	public static ResultJson errorMap(Object data) {
		return new ResultJson(501, "error", data);
	}

	// token验证失败，向页面返回错误信息提示
	public static ResultJson errorTokenMsg(String msg) {
		return new ResultJson(502, msg, null);
	}

	// 服务器捕获到异常，向页面返回错误信息提示
	public static ResultJson errorException(String msg) {
		return new ResultJson(555, msg, null);
	}

	// 默认操作成功
	public ResultJson(Object data) {
		this.status = 200;
		this.msg = "OK";
		this.data = data;
	}

	// 一些自定义返回
	public ResultJson(Integer status, String msg, Object data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	// 一些自定义返回
	public ResultJson(Integer status, String msg) {
		this.status = status;
		this.msg = msg;
	}
	
	//一些自定义返回
	public ResultJson(Integer status, Object data) {
		this.status = status;
		this.data = data;
	}

	/**
	 * @Description:将json结果集转化为ResultJsonMst对象 需要转换的对象是一个类
	 * @param jsondata
	 * @param clazz
	 * @return
	 */
	public static ResultJson formatToPojo(String jsonData, Class<?> clazz) {
		try {
			if (clazz == null) {
				return mapper.readValue(jsonData, ResultJson.class);
			}
			JsonNode jsonNode = mapper.readTree(jsonData);
			JsonNode data = jsonNode.get("data");
			Object obj = null;
			if (clazz != null) {
				if (data.isObject()) {
					obj = mapper.readValue(data.traverse(), clazz);
				} else if (data.isTextual()) {
					obj = mapper.readValue(data.asText(), clazz);
				}
			}
			return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Description: 没有object对象的转化
	 * @param json
	 * @return
	 */
	public static ResultJson format(String json) {
		try {
			return mapper.readValue(json, ResultJson.class);
		} catch (IOException e) {
			logger.debug("没有object对象的json转化失败");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Description Object集合转化需要转换的对象是一个list
	 * @param jsonData
	 * @param clazz
	 * @return
	 */
	public static ResultJson formatToList(String jsonData, Class<?> clazz) {
		try {
			JsonNode jsonNode = mapper.readTree(jsonData);
			JsonNode data = jsonNode.get("data");
			Object obj = null;
			if (data.isArray() && data.size() > 0) {
				obj = mapper.readValue(data.traverse(),
						mapper.getTypeFactory().constructCollectionType(List.class, clazz));
			}
			return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将对象转换成json字符串。
	 * <p>
	 * Title: pojoToJson
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param data
	 * @return
	 */
	public static String objectToJson(Object data) {
		try {
			String string = mapper.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将json结果集转化为对象
	 * 
	 * @param jsonData
	 *            json数据
	 * @param clazz
	 *            对象中的object类型
	 * @return
	 */
	public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
		try {
			T t = mapper.readValue(jsonData, beanType);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将json数据转换成pojo对象list
	 * <p>
	 * Title: jsonToList
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param jsonData
	 * @param beanType
	 * @return
	 */
	public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
		JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, beanType);
		try {
			List<T> list = mapper.readValue(jsonData, javaType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Integer getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public Object getData() {
		return data;
	}

	public static ObjectMapper getMapper() {
		return mapper;
	}

	@Override
	public String toString() {
		return "ResultJson [状态码=" + status + ", 返回的文言=" + msg + ", 数据内容=" + data + "]";
	}

	public static ResultJson managerStateCode(Integer status, Object data) {
		// TODO Auto-generated method stubInteger status, String msg
		return new ResultJson(status, data);
	}

}
