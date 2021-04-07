/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.oxm.castor;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import org.castor.xml.XMLProperties;
import org.exolab.castor.xml.XercesXMLSerializerFactory;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.oxm.xstream.Flight;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xmlunit.builder.Input;
import org.xmlunit.xpath.JAXPXPathEngine;

import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.AbstractMarshallerTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.xmlunit.matchers.CompareMatcher.*;

/**
 * Tests the {@link CastorMarshaller} class.
 *
 * @author Arjen Poutsma
 * @author Jakub Narloch
 * @author Sam Brannen
 */
@Deprecated
public class CastorMarshallerTests {

}
