package com.lapangos.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SocketSettings;

@Configuration
public class SpringMongoConfig {
	private static final Logger LOG = LoggerFactory.getLogger(SpringMongoConfig.class);

	@Value("${spring.data.mongodb.host}")
	private String hostList;
	@Value("${spring.data.mongodb.database}")
	private String database;
	@Value("${spring.data.mongodb.port}")
	private int port;
	@Value("${spring.data.mongodb.username}")
	private String userNameProp;
	@Value("${spring.data.mongodb.passwrd}")
	private String passwordProp;
	@Value("${spring.data.mongodb.connectionsPerHost}")
	private int connPerHost;
	@Value("${spring.data.mongodb.connectTimeout}")
	private int connTimeout;
	@Value("${spring.data.mongodb.minConnPerHost}")
	private int minConnPerHost;
	@Value("${spring.data.mongodb.maxConnIdleTime}")
	private int maxConnIdleTime;
	@Value("${scheduledTime}")
	private String scheduledTime;

	@Autowired
	private Environment environment;

	@Bean
	public MongoClient mongoClient() {

		// myConfig.getSpec();

		String userName = environment.getProperty("USERID");
		LOG.info("Env Variable UserName: " + userName);
		String password = environment.getProperty("PASSWD");
		LOG.info("Env Variable Password: " + password);

		if (userName == null || userName.isEmpty()) {
			userName = userNameProp;
			LOG.info("Properties Variable UserName: " + userName);
		}
		if (password == null || password.isEmpty()) {
			password = passwordProp;
			LOG.info("Properties Variable Password: " + password);
		}

		MongoClient mongoClient = null;

		String[] mongoDbHostList = hostList.trim().split(",");

		List<ServerAddress> serverList = new ArrayList<>();
		for (int i = 0; i < mongoDbHostList.length; i++) {
			ServerAddress serverAddr = new ServerAddress(mongoDbHostList[i], port);
			serverList.add(serverAddr);
		}

		MongoCredential credential = null;
		for (int i = 0; i < mongoDbHostList.length; i++) {

			try {

				credential = MongoCredential.createPlainCredential(userName, "$external", password.toCharArray());

			} catch (Exception e) {
				LOG.error("Exception in SpringMongoConfig.mongoClient: {}", e);
			}
		}

		SocketSettings socketSettings = SocketSettings.builder().connectTimeout(connTimeout, TimeUnit.MILLISECONDS)
				.build();
		ConnectionPoolSettings connSettings = ConnectionPoolSettings.builder()
				.maxConnectionIdleTime(maxConnIdleTime, TimeUnit.MILLISECONDS).maxSize(connPerHost)
				.minSize(minConnPerHost).build();

		WriteConcern MAJORITY = new WriteConcern("majority");

		MongoClientSettings clientSettings = MongoClientSettings.builder().credential(credential).writeConcern(MAJORITY)
				.readPreference(ReadPreference.primaryPreferred())
				.applyToClusterSettings(builder -> builder.hosts(serverList))
				.applyToSocketSettings(builder -> builder.applySettings(socketSettings))
				.applyToConnectionPoolSettings(builder -> builder.applySettings(connSettings)).build();
		mongoClient = MongoClients.create(clientSettings);

		LOG.debug("Mongo Client Started");
		return mongoClient;
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		MongoTemplate mongoTemplate = new MongoTemplate(this.mongoClient(), database);
		MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
		mongoMapping.setCustomConversions(customConversions());
		mongoMapping.afterPropertiesSet();
		return mongoTemplate;
	}

	public MongoCustomConversions customConversions() {
		return new MongoCustomConversions(
				Arrays.asList(new Decimal128ToBigDecimalConverted(), new BigDecimalToDecimal128Converter()));
	}

	public void closeMongoClient() {
		this.mongoClient().close();
	}

	class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {

		@Override
		public Decimal128 convert(BigDecimal source) {
			return new Decimal128(source);
		}
	}

	class Decimal128ToBigDecimalConverted implements Converter<Decimal128, BigDecimal> {

		@Override
		public BigDecimal convert(Decimal128 source) {
			return source.bigDecimalValue();
		}

	}

}
