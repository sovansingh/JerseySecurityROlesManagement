package org.emudhra.com.JerseySecurityRoleManagement.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.util.Base64;

public class SecurityFilter implements ContainerRequestFilter{

	@Context
	private ResourceInfo info;
	@Context
	private HttpHeaders headers;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Method method=info.getResourceMethod();
		if(!method.isAnnotationPresent(PermitAll.class)) {
			if(method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(Response.ok("This Requet con be processed").status(Status.FORBIDDEN).build());
				return;
			}
			List<String> reqHeaders=headers.getRequestHeaders().get("Authorization");
			if(reqHeaders==null || reqHeaders.isEmpty()) {
				requestContext.abortWith(Response.ok("Provide Authorization Header in Request").status(Status.UNAUTHORIZED).build());
				return;
			}
			List<String> userDetails=getUserNameAndPwd(reqHeaders.get(0));
			if(method.isAnnotationPresent(RolesAllowed.class)) {
				List<String> roles = Arrays.asList(method.getAnnotation(RolesAllowed.class).value());
				if(!isValidUser(userDetails, roles)) {
					requestContext.abortWith(Response.ok("Invalid User Details Provided").status(Status.UNAUTHORIZED).build());
					return;
				}else {
					if(!isValidUser(userDetails, Arrays.asList("ALLPERMIN"))) {
						requestContext.abortWith(Response.ok("Invalid User Details Provided").status(Status.UNAUTHORIZED).build());
						return;
					}
				}
			}
		}
	}
	private boolean isValidUser(List<String> userDetails,List<String> roles) {
		boolean flag=false;
		if(userDetails!=null && roles!=null) {
			if("admin".equals(userDetails.get(0)) && "sathya".equals(userDetails.get(1)) && (roles.contains("ALLPERMIT") || roles.contains("EMP")))
				flag=true;
			else if ("emp".equals(userDetails.get(0)) && "sathya".equals(userDetails.get(1)) && (roles.contains("ALLPERMIT") || roles.contains("EMP"))) 
				flag=true;
		}
		return flag;
	}
	private List<String> getUserNameAndPwd(String auth){
		auth=auth.replace("Basic ", "");
		byte[] arr = Base64.decode(auth.getBytes());
		auth=new String(arr);
		StringTokenizer stringTokenizer=new StringTokenizer(auth,":");
		return Arrays.asList(stringTokenizer.nextToken(),stringTokenizer.nextToken());
	}
}