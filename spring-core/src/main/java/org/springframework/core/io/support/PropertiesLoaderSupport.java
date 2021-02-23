/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * 需要从一种或多种资源（不同的资源文件）加载属性的JavaBean样式的组件的基类（配置文件的积累）。
 * 配置可以被复写，还支持本地属性。
 *
 * @author Juergen Hoeller
 * @since 1.2.2
 */
public abstract class PropertiesLoaderSupport {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	protected Properties[] localProperties;

	protected boolean localOverride = false;

	@Nullable
	private Resource[] locations;

	private boolean ignoreResourceNotFound = false;

	@Nullable
	private String fileEncoding;

	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();


	/**
	 * 设置本地属性，例如通过XML bean定义中的“ props”标签。这些可以被视为默认值，这些默认值将被从文件加载的属性覆盖。
	 */
	public void setProperties(Properties properties) {
		this.localProperties = new Properties[] {properties};
	}

	/**
	 *设置本地属性，例如通过XML bean定义中的“ props”标签，可以将多个属性集合并为一个。
	 */
	public void setPropertiesArray(Properties... propertiesArray) {
		this.localProperties = propertiesArray;
	}

	/**
	 *设置要加载的属性文件的位置。
	 *<p>可以指向普通的属性文件或遵循JDK 1.5的属性XML格式的XML文件。
	 */
	public void setLocation(Resource location) {
		this.locations = new Resource[] {location};
	}

	/**
	 *设置要加载的属性文件的位置。
	 * <p>可以指向普通属性文件或遵循JDK 1.5的属性XML格式的XML文件。
	 * <p>注意：在键重叠的情况下，以后文件中定义的属性将覆盖以前文件中定义的属性。
	 * 因此，请确保最具体的文件是给定位置列表中的最后一个文件。
	 */
	public void setLocations(Resource... locations) {
		this.locations = locations;
	}

	/**
	 * 设置本地属性是否覆盖文件中的属性。
	 * <p>默认值为“ false”：文件中的属性将覆盖本地默认值。可以切换为“ true”以使本地属性覆盖文件中的默认值。
	 */
	public void setLocalOverride(boolean localOverride) {
		this.localOverride = localOverride;
	}

	/**
	 * 设置是否找不到属性资源失败。如果属性文件是完全可选的，
	 * 则<p>“ true”是适当的。默认值为“ false”。
	 */
	public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
		this.ignoreResourceNotFound = ignoreResourceNotFound;
	}

	/**
	 * 设置用于解析属性文件的编码。
	 * <p>默认为无，使用{@code java.util.Properties}默认编码。
	 * <p>仅适用于普通的属性文件，不适用于XML文件。
	 * @see org.springframework.util.PropertiesPersister#load
	 */
	public void setFileEncoding(String encoding) {
		this.fileEncoding = encoding;
	}

	/**
	 * 设置PropertiesPersister以用于解析属性文件。默认值为DefaultPropertiesPersister。
	 * @see org.springframework.util.DefaultPropertiesPersister
	 */
	public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
		this.propertiesPersister =
				(propertiesPersister != null ? propertiesPersister : new DefaultPropertiesPersister());
	}


	/**
	 * 返回一个合并的Properties实例，其中包含已加载的属性和在此FactoryBean上设置的属性。
	 */
	protected Properties mergeProperties() throws IOException {
		Properties result = new Properties();

		if (this.localOverride) {
			// Load properties from file upfront, to let local properties override.
			loadProperties(result);
		}

		if (this.localProperties != null) {
			for (Properties localProp : this.localProperties) {
				CollectionUtils.mergePropertiesIntoMap(localProp, result);
			}
		}

		if (!this.localOverride) {
			// Load properties from file afterwards, to let those properties override.
			loadProperties(result);
		}

		return result;
	}

	/**
	 * 将属性加载到给定实例中。
	 * @param props the Properties instance to load into
	 * @throws IOException in case of I/O errors
	 * @see #setLocations
	 */
	protected void loadProperties(Properties props) throws IOException {
		if (this.locations != null) {
			for (Resource location : this.locations) {
				if (logger.isTraceEnabled()) {
					logger.trace("Loading properties file from " + location);
				}
				try {
					PropertiesLoaderUtils.fillProperties(
							props, new EncodedResource(location, this.fileEncoding), this.propertiesPersister);
				}
				catch (FileNotFoundException | UnknownHostException | SocketException ex) {
					if (this.ignoreResourceNotFound) {
						if (logger.isDebugEnabled()) {
							logger.debug("Properties resource not found: " + ex.getMessage());
						}
					}
					else {
						throw ex;
					}
				}
			}
		}
	}

}
