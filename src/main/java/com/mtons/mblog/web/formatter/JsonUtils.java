package com.mtons.mblog.web.formatter;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * JSON 序列化 / 反序列化工具类
 * <p>
 * 职责说明：
 * <ul>
 *   <li>基于 Jackson {@link ObjectMapper} 封装统一的 JSON 序列化与反序列化能力；</li>
 *   <li>提供默认的日期格式化策略（{@code yyyy-MM-dd}），并支持通过
 *       {@link DateTimeFormat} 注解自定义日期序列化格式；</li>
 *   <li>通过自定义 {@link JacksonAnnotationIntrospector} 识别方法上的
 *       {@link DateTimeFormat} 注解，从而动态选择对应的日期序列化器。</li>
 * </ul>
 *
 * 敏感字段过滤策略说明：
 * <ul>
 *   <li>当前工具类未内置敏感字段过滤逻辑，如需对敏感字段（如密码、token 等）进行过滤，
 *       可在目标 POJO 上使用 Jackson 提供的 {@code @JsonIgnore} / {@code @JsonProperty(access = WRITE_ONLY)}
 *       注解控制字段的序列化行为；</li>
 *   <li>本类作为 Spring {@link Component} 注册到容器中，便于其他组件注入使用。</li>
 * </ul>
 *
 * @see ObjectMapper
 * @see DateTimeFormat
 * @see JsonSerializer
 */
@Component
public class JsonUtils {
	/** 默认日期序列化格式（yyyy-MM-dd） */
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	/** 全局共享的 ObjectMapper 实例（在静态代码块中初始化） */
	private static final ObjectMapper mapper;

	/**
	 * 获取全局共享的 ObjectMapper 实例
	 *
	 * @return ObjectMapper 实例
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

	static {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

		mapper = new ObjectMapper();
		// 设置默认日期格式
		mapper.setDateFormat(dateFormat);
		// 通过自定义注解内省器，识别方法上的 @DateTimeFormat 注解，动态选择对应的日期序列化器
		mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
			private static final long serialVersionUID = -5854941510519564900L;

			@Override
			public Object findSerializer(Annotated a) {
				if (a instanceof AnnotatedMethod) {
					AnnotatedElement m = a.getAnnotated();
					DateTimeFormat an = m.getAnnotation(DateTimeFormat.class);
					if (an != null) {
						// 若注解中指定的格式与默认格式不同，则使用自定义序列化器
						if (!DEFAULT_DATE_FORMAT.equals(an.pattern())) {
							return new JsonDateSerializer(an.pattern());
						}
					}
				}
				return super.findSerializer(a);
			}
		});
	}

	/**
	 * 将任意 Java 对象序列化为 JSON 字符串
	 *
	 * @param obj 待序列化的对象
	 * @return JSON 字符串
	 * @throws RuntimeException 序列化失败时抛出运行时异常
	 */
	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("对象转换为 JSON 字符串失败!");
		}
	}

	/**
	 * 将 JSON 字符串反序列化为指定类型的 Java 对象
	 *
	 * @param json  JSON 字符串
	 * @param clazz 目标类型的 Class 对象
	 * @param <T>   目标类型泛型
	 * @return 反序列化后的对象
	 * @throws RuntimeException 反序列化失败时抛出运行时异常
	 */
	public <T> T toObject(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new RuntimeException("JSON 字符串转换为对象失败!");
		}
	}

	/**
	 * 自定义日期序列化器
	 * <p>
	 * 用于在 JSON 序列化过程中，按照指定格式输出日期字符串，
	 * 配合 {@link DateTimeFormat} 注解使用。
	 */
	public static class JsonDateSerializer extends JsonSerializer<Date> {
		/** 日期格式化器 */
		private SimpleDateFormat dateFormat;

		/**
		 * 构造方法：根据传入的格式串创建日期格式化器
		 *
		 * @param format 日期格式串，如 {@code yyyy-MM-dd HH:mm:ss}
		 */
		public JsonDateSerializer(String format) {
			dateFormat = new SimpleDateFormat(format);
		}

		/**
		 * 执行日期序列化：将 Date 对象按指定格式格式化为字符串并写入 JSON 输出流
		 *
		 * @param date     待序列化的日期对象
		 * @param gen      JSON 生成器
		 * @param provider 序列化上下文
		 * @throws IOException              IO 异常
		 * @throws JsonProcessingException JSON 处理异常
		 */
		@Override
		public void serialize(Date date, JsonGenerator gen,
				SerializerProvider provider) throws IOException,
				JsonProcessingException {
			String value = dateFormat.format(date);
			gen.writeString(value);
		}
	}
}
