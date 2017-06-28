package cn.whaley.datawarehouse.illidan.server.util;

import cn.whaley.datawarehouse.illidan.server.domain.FormFieldKeyValuePair;
import cn.whaley.datawarehouse.illidan.server.domain.UploadFileItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 创建人：郭浩
 * 创建时间：2017/06/26
 * 程序作用：
 * 数据输入：
 * 数据输出：
 */
public class HttpClientUtil {
	private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
	// 每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
	private static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";
	/**
	 * 定义编码格式 UTF-8 
	 */
	public static final String URL_PARAM_DECODECHARSET_UTF8 = "UTF-8";
	/**
	 * 定义编码格式 GBK 
	 */
	private static final String URL_PARAM_CONNECT_FLAG = "&";
	private static final String EMPTY = "";
	private static MultiThreadedHttpConnectionManager connectionManager = null;  
    private static int connectionTimeOut = 25000;  
    private static int socketTimeOut = 25000;  
    private static int maxConnectionPerHost = 20;  
    private static int maxTotalConnections = 20;
	private static HttpClient client;
	static{  
        connectionManager = new MultiThreadedHttpConnectionManager();  
        connectionManager.getParams().setConnectionTimeout(connectionTimeOut);  
        connectionManager.getParams().setSoTimeout(socketTimeOut);  
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);  
        connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);  
        client = new HttpClient(connectionManager);  
    }
	
	public static String URLPost(String url, Map<String, String> params, String enc){
		
		String response = EMPTY;
		PostMethod postMethod = null;

		try {
			postMethod = new PostMethod(url);

			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
			//将表单的值放入postMethod中
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				String value = params.get(key);
				postMethod.addParameter(key, value);
			}



			//执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if(statusCode == HttpStatus.SC_OK) {  
                response = postMethod.getResponseBodyAsString();  
            }else{  
                log.error("响应状态码 = " + postMethod.getStatusCode());  
            }  
		}catch (HttpException  e) {
			log.error("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
			e.printStackTrace();
			
		}catch(IOException e) {
			log.error("发生网络异常", e);
			e.printStackTrace();
			
		}finally{
			if(postMethod != null){ 
				if(postMethod != null){ 
					postMethod = null;
				}
			}
		}
		return response;
	}

	public static String URLPostWithJson(String url, String json){

		String response = EMPTY;
		PostMethod postMethod = null;

		try {
			postMethod = new PostMethod(url);

			postMethod.setRequestHeader("Content-Type","application/json;charset=UTF-8;");
			postMethod.setRequestBody(json);
			//执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if(statusCode == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			}else{
				log.error("响应状态码 = " + postMethod.getStatusCode());
			}
		}catch (HttpException  e) {
			log.error("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
			e.printStackTrace();

		}catch(IOException e) {
			log.error("发生网络异常", e.getMessage());
//			e.printStackTrace();

		}finally{
			if(postMethod != null){
				if(postMethod != null){
					postMethod = null;
				}
			}
		}
		return response;
	}
	
	public static String URLGet(String url, Map<String, String> params, String enc){
		String response = EMPTY;  
        GetMethod getMethod = null;       
        StringBuffer strtTotalURL = new StringBuffer(EMPTY);
        if(strtTotalURL.indexOf("?") == -1){
        	strtTotalURL.append(url).append("?").append(getUrl(params, enc));
        }else{
        	strtTotalURL.append(url).append("&").append(getUrl(params, enc));
        }
        log.debug("GET请求URL = \n" + strtTotalURL.toString());
        try {
        	getMethod = new GetMethod(strtTotalURL.toString());
        	getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
        	int statusCode = client.executeMethod(getMethod);
        	if(statusCode == HttpStatus.SC_OK){
        		response = getMethod.getResponseBodyAsString();
        	}else{
        		log.debug("响应状态码 = " + getMethod.getStatusCode());
        	}
        	
        } catch (HttpException e) {
        	log.error("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
        	e.printStackTrace();
		}catch (IOException e) {
			log.error("发生网络异常", e);
			e.printStackTrace();
		}finally{  
            if(getMethod != null){  
                getMethod.releaseConnection();  
                getMethod = null;  
            }  
        }
          
        return response;
	}

	private static String getUrl(Map<String, String> map, String valueEnc) {
		if (null == map || map.keySet().size() == 0) {
			return EMPTY;
		}
		StringBuffer url = new StringBuffer();
		Set<String> keys = map.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();){
			String key = it.next();
			if (map.containsKey(key)) {
				String val = map.get(key);
				String str = val != null ? val : EMPTY;
				try {
					str = URLEncoder.encode(str, valueEnc);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				url.append(key).append("=").append(str).append(URL_PARAM_CONNECT_FLAG);
			}
		}
		String strURL = EMPTY;
		strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals(EMPTY + strURL.charAt(strURL.length() - 1))) {  
            strURL = strURL.substring(0, strURL.length() - 1);  
        }
		return strURL;
	}

	/**
	 *
	 * @param serverUrl 提交的url
	 * @param generalFormFields 设置字段
	 * @param filesToBeUploaded 提交的文本等
	 * @return
	 * @throws Exception
	 */
	public static String postForm(String serverUrl,
								  ArrayList<FormFieldKeyValuePair> generalFormFields,
								  ArrayList<UploadFileItem> filesToBeUploaded){

		JSONObject resultJson = new JSONObject();
		try {
			// 向服务器发送post请求
			URL url = new URL(serverUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 发送POST请求必须设置如下两行
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			// 头
			String boundary = BOUNDARY;
			// 传输内容
			StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);
			// 尾
			String endBoundary = "\r\n--" + boundary + "--\r\n";
			OutputStream out = connection.getOutputStream();
			// 1. 处理文字形式的POST请求
			for (FormFieldKeyValuePair ffkvp : generalFormFields){
				contentBody.append("\r\n")
						.append("Content-Disposition: form-data; name=\"")
						.append(ffkvp.getKey() + "\"")
						.append("\r\n")
						.append("\r\n")
						.append(ffkvp.getValue())
						.append("\r\n")
						.append("--")
						.append(boundary);
			}
			String boundaryMessage1 = contentBody.toString();
			out.write(boundaryMessage1.getBytes("utf-8"));
			// 2. 处理文件上传
			for (UploadFileItem ufi : filesToBeUploaded){
				contentBody = new StringBuffer();
				contentBody.append("\r\n")
						.append("Content-Disposition:form-data; name=\"")
						.append(ufi.getFormFieldName() + "\"; ") // form中field的名称
						.append("filename=\"")
						.append(ufi.getFileName() + "\"") // 上传文件的文件名，包括目录
						.append("\r\n")
						.append("Content-Type:application/octet-stream")
						.append("\r\n\r\n");
				String boundaryMessage2 = contentBody.toString();
				out.write(boundaryMessage2.getBytes("utf-8"));
				// 开始真正向服务器写文件
				File file = new File(ufi.getFileName());
				DataInputStream dis = new DataInputStream(new FileInputStream(file));
				int bytes = 0;
				byte[] bufferOut = new byte[(int) file.length()];
				bytes = dis.read(bufferOut);
				out.write(bufferOut, 0, bytes);
				dis.close();
				contentBody.append("------------HV2ymHFg03ehbqgZCaKO6jyH");
				String boundaryMessage = contentBody.toString();
				out.write(boundaryMessage.getBytes("utf-8"));
			}
			out.write("------------HV2ymHFg03ehbqgZCaKO6jyH--\r\n".getBytes("UTF-8"));
			// 3. 写结尾
			out.write(endBoundary.getBytes("utf-8"));
			out.flush();
			out.close();
			// 4. 从服务器获得回答的内容
			String strLine = "";
			String strResponse = "";
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((strLine = reader.readLine()) != null){
				strResponse += strLine + "\n";
			}
			resultJson.put("status","success");
			resultJson.put("message",strResponse);
			log.info("upload project zip success ...");
		}catch (Exception e){
			resultJson.put("status","error");
			resultJson.put("message",e.getMessage());
			log.info("upload project zip error : " + e.getMessage());
		}
		return resultJson.toString();
	}

	
}
