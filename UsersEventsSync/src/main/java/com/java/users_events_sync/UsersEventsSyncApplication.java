package com.java.users_events_sync;

import static com.java.users_events_sync.util.ConstantUtils.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


@SpringBootApplication
public class UsersEventsSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersEventsSyncApplication.class, args);
	}
	
	@Bean
	public MongoClient getMongoConnectivity() {
		
		String url = "mongodb+srv://"+getUsername()+":"+getPassword()+"@cluster0."+getClusterName()+".mongodb.net/"+getDatabaseName()+"?retryWrites=true&w=majority";
		
		MongoClient mongoClient = MongoClients.create(url);
		
		return mongoClient;
	}
}
