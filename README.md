Encode Enforcer

Maven Enforcer Plugin custom rule for enforcing file encodings.

To Install :

1.  git clone https://github.com/mikedon/encoding-enforcer.git

2.  cd encoding-enforcer

3.  mvn clean install

Example Usage:

```
<properties>
	<!-- The rule uses this property to validate file encodings against -->
	<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

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
			            	<!-- Validate files match this encoding. -->
			            	<encoding>UTF-8</encoding>
			            </encodingRule>
          			</rules>
				</configuration>
			</execution>
        </executions>
	    </plugin>   
	</plugins>
</build>
```
