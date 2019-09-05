/******************************************************************************
 *  Compilation:  javac HelloWorld.java
 *  Execution:    java HelloWorld
 *
 *  Prints "Hello, World". By tradition, this is everyone's first program.
 *
 *  % java HelloWorld
 *  Hello, World
 *
 *  These 17 lines of text are comments. They are not part of the program;
 *  they serve to remind us about its properties. The first two lines tell
 *  us what to type to compile and test the program. The next line describes
 *  the purpose of the program. The next few lines give a sample execution
 *  of the program and the resulting output. We will always include such 
 *  lines in our programs and encourage you to do the same.
 *
 ******************************************************************************/

            package


// this comment doesn't move!
 notVeryGoodPackage                                             ; 			// this comment doesn't move!



 // this comment stays

                    // so does this one

// and this one

                 // this one uses spaces
					// but this one uses tabs

public class HelloWorld { // comment
	
	private String name;
	private long age;
	public String ssn;
    long attributeDefault;

    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        System.out.println("Hello, World");
    }

    public void uselessMethod() {				// useless comment
        // does nothing of note
    }

    // method with weird indentation
        public int returnTwo() {
        	int notAMainAttribute = 2;
            return notAMainAttribute;
        }
        


}

class VeryEmpty {

    // This class intentionally left blank

}
