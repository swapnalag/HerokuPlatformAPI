package jp.co.flect.heroku.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.co.flect.heroku.json.JsonUtils;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class HttpClientTransport implements Transport {
	
	private HttpClient client = null;
	private ProxyInfo proxyInfo = null;
	
	private int soTimeout = 0;
	private int connectionTimeout = 0;
	
	public HttpResponse execute(HttpRequest request) throws IOException {
		HttpClient client = getHttpClient();
		HttpUriRequest method = null;
		switch (request.getMethod()) {
			case POST:
				method = new HttpPost(request.getUrl());
				break;
			case GET:
				method = new HttpGet(request.getUrl());
				break;
			case PATCH:
				method = new HttpPatch(request.getUrl());
				break;
			case PUT:
				method = new HttpPut(request.getUrl());
				break;
			case DELETE:
				method = new HttpDelete(request.getUrl());
				break;
		} 
		for (Map.Entry<String, String[]> entry : request.getHeaders().entrySet()) {
			String key = entry.getKey();
			for (String s : entry.getValue()) {
				method.addHeader(key, s);
			}
		}
		if (request.getFileBody() != null) {
			((HttpEntityEnclosingRequestBase)method).setEntity(new FileEntity(request.getFileBody()));
		} else if (request.getParameters().size() > 0) {
			if ("application/json".equals(request.getFirstHeader("content-type"))) {
				String json = JsonUtils.serialize(request.getParameters());
				((HttpEntityEnclosingRequestBase)method).setEntity(new StringEntity(json, "utf-8"));
			} else {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				for (Map.Entry<String, Object> entry : request.getParameters().entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (value instanceof String) {
						list.add(new BasicNameValuePair(key, value.toString()));
					} else if (value instanceof String[]) {
						key += "[]";
						for (String s : (String[])value) {
							list.add(new BasicNameValuePair(key, s));
						}
					}
				}
				((HttpPost)method).setEntity(new UrlEncodedFormEntity(list, "utf-8"));
			}
		}
		return new HttpResponseWrapper(client.execute(method));
	}
	
	public ProxyInfo getProxyInfo() { return this.proxyInfo;}
	public void setProxyInfo(ProxyInfo proxy) { this.proxyInfo = proxy;}
	
	private HttpClient getHttpClient() {
		if (this.client == null) {
			BasicHttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, this.connectionTimeout);
			HttpConnectionParams.setSoTimeout(params, this.soTimeout);
		
			DefaultHttpClient client = new DefaultHttpClient(params);
			if (this.proxyInfo != null) {
				HttpHost proxy = new HttpHost(proxyInfo.getHost(), proxyInfo.getPort());
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				if (proxyInfo.getUserName() != null && proxyInfo.getPassword() != null) {
					client.getCredentialsProvider().setCredentials(
						new AuthScope(proxyInfo.getHost(), proxyInfo.getPort()),
						new UsernamePasswordCredentials(proxyInfo.getUserName(), proxyInfo.getPassword()));
				}
			}
			this.client = client;
		}
		return this.client;
	}
	
	public int getSoTimeout() { return this.soTimeout;}
	public void setSoTimeout(int n) { this.soTimeout = n;}
	
	public int getConnectionTimeout() { return this.connectionTimeout;}
	public void setConnectionTimeout(int n) { this.connectionTimeout = n;}
	
	public static class Factory extends TransportFactory {
		
		public Transport create() { return new HttpClientTransport();}
		
	}
	
	private static class HttpResponseWrapper implements HttpResponse {
		
		private org.apache.http.HttpResponse res;
		private String body;
		
		public HttpResponseWrapper(org.apache.http.HttpResponse res) {
			this.res = res;
		}
		
		public int getStatus() { 
			return this.res.getStatusLine().getStatusCode();
		}
		
		public String getContentType() {
			String ret = getHeader("content-type");
			return ret == null ? "text/plain" : ret;
		}
		
		public String getBody() {
			if (this.body == null) {
				try {
					this.body = EntityUtils.toString(this.res.getEntity(), "utf-8");
				} catch (IOException e) {
					this.body = e.toString();
				}
			}
			return this.body;
		}
		
		public String getHeader(String name) {
			Header h = res.getFirstHeader(name);
			return h == null ? null : h.getValue();
		}
		
	}
}

