package com.zaumal.core.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xbean.spring.context.impl.DateEditor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.ServletContextAware;

import com.alibaba.fastjson.JSON;

public abstract class AbstractController implements ServletContextAware {
	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * 输出字节
	 * 
	 * @param response
	 * @param bytes
	 */
	protected final void responseBytes(byte[] bytes, HttpServletResponse response) {
		try {
			OutputStream output = response.getOutputStream();
			output.write(bytes);
			output.flush();
			response.setContentLength(bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将bean表按json格式输出
	 * 
	 * @param result
	 * @param response
	 */
	protected final void returnUTF8Json(Object result, HttpServletResponse response) {
		returnUTF8Json(JSON.toJSONString(result), response);
	}
	
	protected final void returnUTF8Json(String result, HttpServletResponse response) {
		responseUTF8String(result, response);
	}
	
	protected final void returnGBKJson(Object result, HttpServletResponse response) {
		returnGBKJson(JSON.toJSONString(result), response);
	}
	
	protected final void returnGBKJson(String result, HttpServletResponse response) {
		returnGBKString(result, response);
	}
	
	/**
	 * 按UTF-8格式输出字符串
	 * 
	 * @param str （需要输出的字符串）
	 * @param contentType （内容类型）
	 * @param response
	 */
	private final void responseUTF8String(String str, HttpServletResponse response) {
		if (StringUtils.isEmpty(str)) {
			return;
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		byte[] bs;
		try {
			bs = str.getBytes("UTF-8");// 将字符串转换为字节
			responseBytes(bs,response);// 调用输出字节的方法
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private final void returnGBKString(String json, HttpServletResponse response){
		if (StringUtils.isEmpty(json)) {
			return;
		}
		String tmp = null;
		try {
			tmp  = new String(json.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding("gbk");
		response.setContentType("application/json;charset=gbk");
		byte[] bs;
		try {
			bs = tmp.getBytes("gbk");// 将字符串转换为字节
			responseBytes(bs,response);// 调用输出字节的方法
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重定向
	 * 
	 * @param url
	 */
	protected final void redirect(String url, HttpServletResponse response){
		try{
			response.sendRedirect(url);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		// 对于需要转换为Date类型的属性，使用DateEditor进行处理
		binder.registerCustomEditor(Date.class, new DateEditor());
	}
}
