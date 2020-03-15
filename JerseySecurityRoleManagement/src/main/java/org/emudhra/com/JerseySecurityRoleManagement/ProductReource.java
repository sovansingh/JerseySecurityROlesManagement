package org.emudhra.com.JerseySecurityRoleManagement;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/product")
public class ProductReource {

	@GET
	@Path("/all")
	@PermitAll
	public String showAll() {
		return "Hello !! Al";
	}
	
	@GET
	@Path("/admin")
	@RolesAllowed("ADMIN")
	public String showAdmin() {
		return "Hello Admin";
	}
	
	@GET
	@Path("/emp")
	@RolesAllowed("EMPLOYEE")
	public String showEmp() {
		return "Hello Employee";
	}
	
	@GET
	@Path("/noperm")
	@DenyAll
	public String showNone() {
		return "Hello None";
	}
}
