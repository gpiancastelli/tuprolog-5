package alice.tuprolog;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a Java method representing a Prolog directive in a library.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Directive {

}
