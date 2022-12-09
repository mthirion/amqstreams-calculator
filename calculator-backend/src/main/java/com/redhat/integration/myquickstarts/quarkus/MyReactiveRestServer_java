package com.redhat.integration.myquickstarts.quarkus;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/api/myreactiveresource")
public class MyReactiveRestServer {
    
	private MyJsonModel example;

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public Uni<Response> getOneResourceReactive(@PathParam String id) {
		// code here
		
		MyJsonModel data = new MyJsonModel();
		return Uni.createFrom().item(Response.ok(data).build());
		/* Example of transforming he response data
		 * .onItem().transform(f -> f != null ? Response.ok(f.await().indefinitely()) : Response.ok(null))
		 */
	}    
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getResourceListReactive() {
		Set<MyJsonModel> mylist = Collections.synchronizedSet(new LinkedHashSet<>());
		// code here
		
		mylist.add(new MyJsonModel());
		return Uni.createFrom().item(Response.ok(mylist).build());
	}  
	
/*	Uni is lazy 				-> the operation is not triggered when the Uni is returned but when the Uni is subscribed to
 *  CompletionStage is eager	-> the operation is triggered then the CompletionStage object is returned, even though there might not be data yet
 *  
 *  CompletionStage is cached	-> multiple call returns the same data
 *  Uni is not cached			-> multiple call triggers multiple actions on the target
 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CompletionStage<Response> getResourceListReactiveWithStage() {
		Set<MyJsonModel> mylist = Collections.synchronizedSet(new LinkedHashSet<>());
		// code here
		
		mylist.add(new MyJsonModel());
		return Uni.createFrom().item(Response.ok(mylist).build()).subscribeAsCompletionStage();
	}  	
*/
	
/*
 * Returning a Multi means returning multiple objects one by one
 * (The example below actually only returns one piece but should return multiple ones)
  
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Multi<Response> getResourceListReactiveAsChunks() {
		Set<MyJsonModel> mylist = Collections.synchronizedSet(new LinkedHashSet<>());
		// code here
		
		mylist.add(new MyJsonModel());
		return Multi.createFrom().item(Response.ok(mylist).build());
		
	}  
*/
		
}
