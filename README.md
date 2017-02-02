# proactive-iam

## Give it a try:

```bash
./gradlew jettyRunWar
```

Embedded jetty listen on port 9000 by default. Context path is '/'.

## Generate a hash for a given password

### From cmdline tool

java -jar tools/shiro-tools-hasher-1.3.2-cli.jar --algorithm SHA-256 --format shiro1 --gensalt --gensaltsize 256 --iterations 1024 --password

### Programmatically

```java
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

//We'll use a Random Number Generator to generate salts.  This 
//is much more secure than using a username as a salt or not 
//having a salt at all.  Shiro makes this easy. 
//
//Note that a normal app would reference an attribute rather 
//than create a new RNG every time: 
RandomNumberGenerator rng = new SecureRandomNumberGenerator();
Object salt = rng.nextBytes();

//Now hash the plain-text password with the random salt and multiple 
//iterations and then Base64-encode the value (requires less space than Hex): 
String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, salt, 1024).toBase64();
```

