package com.googlecode.mjorm.spring;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

/**
 * {@link FactoryBean} for creating {@link Mongo} objects.
 */
public class MongoFactoryBean
	extends AbstractFactoryBean<Mongo> {

	public static final int DEFAULT_CONNECTIONS_PER_HOST 		= 10;
	public static final int DEFAULT_THREADS_ALLOWED_TO_BLOCK	= 5;
	public static final int DEFAULT_MAX_WAIT_TIME				= 1000 * 60 * 2;
	public static final int DEFAULT_CONNECT_TIMEOUT				= 0;
	public static final int DEFAULT_SOCKET_TIMEOUT				= 0;
	public static final boolean DEFAULT_AUTO_CONNECT_RETRY		= false;
	public static final boolean DEFAULT_CLOSE_ON_DESTROY		= true;

	private String host;
	private int port;

	private int connectionsPerHost								= DEFAULT_CONNECTIONS_PER_HOST;
	private int threadsAllowedToBlockForConnectionMultiplier	= DEFAULT_THREADS_ALLOWED_TO_BLOCK;
	private int maxWaitTime 									= DEFAULT_MAX_WAIT_TIME;
	private int connectTimeout 									= DEFAULT_CONNECT_TIMEOUT;
	private int socketTimeout 									= DEFAULT_SOCKET_TIMEOUT;
	private boolean autoConnectRetry 							= DEFAULT_AUTO_CONNECT_RETRY;
	private boolean closeOnDestroy								= DEFAULT_CLOSE_ON_DESTROY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Mongo createInstance()
		throws Exception {

		// setup options
		MongoOptions mongoOptions 		= new MongoOptions();
		mongoOptions.connectionsPerHost = connectionsPerHost;
		mongoOptions.maxWaitTime 		= maxWaitTime;
		mongoOptions.connectTimeout 	= connectTimeout;
		mongoOptions.socketTimeout 		= socketTimeout;
		mongoOptions.autoConnectRetry 	= autoConnectRetry;
		mongoOptions.threadsAllowedToBlockForConnectionMultiplier
			= threadsAllowedToBlockForConnectionMultiplier;

		// create and return Mongo
		return new Mongo(
			new ServerAddress(host, port),
			mongoOptions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getObjectType() {
		return Mongo.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void destroyInstance(Mongo instance)
		throws Exception {
		if (closeOnDestroy) {
			instance.close();
		}
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param connectionsPerHost the connectionsPerHost to set
	 */
	public void setConnectionsPerHost(int connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	/**
	 * @param threadsAllowedToBlockForConnectionMultiplier the threadsAllowedToBlockForConnectionMultiplier to set
	 */
	public void setThreadsAllowedToBlockForConnectionMultiplier(int threadsAllowedToBlockForConnectionMultiplier) {
		this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
	}

	/**
	 * @param maxWaitTime the maxWaitTime to set
	 */
	public void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	/**
	 * @param connectTimeout the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @param socketTimeout the socketTimeout to set
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	/**
	 * @param autoConnectRetry the autoConnectRetry to set
	 */
	public void setAutoConnectRetry(boolean autoConnectRetry) {
		this.autoConnectRetry = autoConnectRetry;
	}

}
