/******************************************************************************
 *  Compilation:  javac HelloWorld.java
 *  Execution:    java HelloWorld
 *
 *  Prints "Hello World!".
 *
 *  % java HelloWorld
 *  Hello World!
 *
 ******************************************************************************/

            package


// this comment doesn't move!
 firstPackage                                             ; 			// this comment doesn't move!



 // this comment stays

                    // so does this one

// and this one

                 // this one uses spaces
					// but this one uses tabs

import java.io.File;
import org.apache.commons.io.FileUtils;
import com.github.javaparser.*;
import secondPackage.GoodbyeWorld;

public class HelloWorldRemastered { // comment
	
	private String name;
	private int age;
 int attributeDefault;

    public static void main(String[] args) {
        // Prints "Hello World!" to the terminal window.
        System.out.println("Hello World!");
    }

    public int veryFastMethod() {				// useless comment
        // does nothing of note
    }

    // method with weird indentation
        public int returnTwo() {
        	int notAMainAttribute = 2;
            return notAMainAttribute;
        }
        


}

class Empty {

    // This class intentionally left blank

}

class NotEmpty {

    private int importantNumber;

    public int getImportantNumber() {
        return importantNumber;
    }
}
