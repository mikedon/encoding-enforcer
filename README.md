Encode Enforcer

Maven Enforcer Plugin custom rule for enforcing file encodings.

Example Usage:

```
<build>
	<plugins>
		<plugin>
	        <groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-enforcer-plugin</artifactId>
			<version>1.2</version>
			<dependencies>
				<dependency>
					<groupId>com.miked</groupId>
					<artifactId>encoding-enforcer</artifactId>
					<version>1.0</version>
				</dependency>
			</dependencies>
			<executions>
			<execution>
				<id>enforce-encoding</id>
				<goals>
					<goal>enforce</goal>
				</goals>
				<configuration>
					<rules>
			            <encodingRule implementation="com.miked.maven.enforcer.rule.EncodingRule">
							<!-- Directory to check for files -->
			            	<directory>relative/path</directory>
							<!-- Regular expression to match file names against -->
			            	<includes>.*\.properties</includes>
			            </encodingRule>
          			</rules>
				</configuration>
			</execution>
        </executions>
	    </plugin>   
	</plugins>
</build>
```
