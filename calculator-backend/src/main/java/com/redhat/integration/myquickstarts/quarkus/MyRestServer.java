package com.redhat.integration.myquickstarts.quarkus;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.redhat.integration.myquickstarts.quarkus.model.*;

import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

//import java.sql.Connection;
//import java.sql.ResultSet;
//import javax.inject.Inject;
//import io.agroal.api.AgroalDataSource;

@Path("/api/kafkasizing")
public class MyRestServer {
    	
	@HEAD
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTextResource() {
		
		return Response.ok().build();
	}    	  
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createResource(KafkaRequirements model) {
		
		KafkaClusterDesc desc = KafkaCalculator.compute(model);

		return Response.ok(desc).build();
	}
		
}
