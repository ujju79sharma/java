package com.java.gigturbo;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import static com.java.gigturbo.util.ConstantUtils.*;

@SpringBootApplication
public class GigTurboApplication {

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		SpringApplication.run(GigTurboApplication.class, args);
	}
	
	@Bean
	public MongoClient getMongoConnectivity() {
		
		String url = "mongodb+srv://"+getUsername()+":"+getPassword()+"@cluster0."+getClusterName()+".mongodb.net/"+getDatabaseName()+"?retryWrites=true&w=majority";
		
		MongoClient mongoClient = MongoClients.create(url);
		
		return mongoClient;
	}
}