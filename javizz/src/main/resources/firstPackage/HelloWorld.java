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
 notVeryGoodPackage                                             ; 			// this comment doesn't move!



 // this comment stays

                    // so does this one

// and this one

                 // this one uses spaces
					// but this one uses tabs

public class HelloWorld { // comment
	
	private String name;
	private int age;
	public long ssn;
    long attributeDefault;

    public static void main(String[] args) {
        // Prints "Hello World!" to the terminal window.
        System.out.println("Hello World!");
    }

    public long uselessMethod() {				// useless comment
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
