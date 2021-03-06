/*
 * Copyright 2014 Black Pepper Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.blackpepper.common.spring.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import com.google.common.base.Joiner;

import static java.util.Collections.singleton;

import static com.google.common.base.Preconditions.checkNotNull;

import static uk.co.blackpepper.common.spring.convert.CollectionDelimiters.getDelimiter;

/**
 * Spring converter to convert a collection to a string.
 * <p>
 * Each element is converted to a string using the specified conversion service and then concatenated using the
 * configured delimiter. The delimiter is specified by using the {@code @CollectionDelimiter} annotation on the target
 * type which defaults to a comma if not present.
 * <p>
 * This class differs from {@code org.springframework.core.convert.support.CollectionToStringConverter} in that it
 * allows customisation of the delimiter.
 * 
 * @see CollectionDelimiter
 */
public class CollectionToStringConverter implements GenericConverter {

	private final ConversionService conversionService;

	public CollectionToStringConverter(ConversionService conversionService) {
		this.conversionService = checkNotNull(conversionService, "conversionService");
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return singleton(new ConvertiblePair(Collection.class, String.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Collection<?> sourceCollection = (Collection<?>) source;
		List<String> targetCollection = new ArrayList<>();
		
		for (Object sourceElement : sourceCollection) {
			String targetElement = (String) conversionService.convert(sourceElement,
				sourceType.getElementTypeDescriptor(), TypeDescriptor.valueOf(String.class));
			
			targetCollection.add(targetElement);
		}
		
		return Joiner.on(getDelimiter(sourceType)).join(targetCollection);
	}
}
