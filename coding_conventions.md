# Coding Conventions #

  * don't return a value from a method if it is not used afterwards
  * utf-8 encoding
  * unix newline encoding (only \n)
  * formatting according to the Eclipse Indigo formatter with a maximum of 150 chars per line(import via Window->Preferences->Java->Formatter->Import...)
  * suitable names for methods/variables/classes in CamelCase
  * Interface-classes start with an "I" in front of the actual name
  * minimal english Javadoc for every method
  * no single line IF / FOR without braces
  * no magic numbers - introduce constants with an appropriate name
  * don't use System.out.err() but use Logger instead