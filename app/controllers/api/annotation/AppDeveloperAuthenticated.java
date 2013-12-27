package controllers.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;
import controllers.api.auth.AppDeveloperAuthController;
import controllers.api.auth.CorsController;

@With({CorsController.class, AppDeveloperAuthController.class})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppDeveloperAuthenticated {
	boolean value() default true;
}
