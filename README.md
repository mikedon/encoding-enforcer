DEPRECATED
==========

Prefer [Require Encoding](http://www.mojohaus.org/extra-enforcer-rules/requireEncoding.html) from [Mojohaus' Extra Enforcer Rules](http://www.mojohaus.org/extra-enforcer-rules/).

Encode Enforcer
===============

Maven Enforcer Plugin custom rule for enforcing file encodings.

To Install
----------

1. `git clone https://github.com/mikedon/encoding-enforcer.git`

1. `cd encoding-enforcer`

1. `mvn clean install`

Example Usage
-------------
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
          <version>2.0</version>
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
              <requireEncoding implementation="com.miked.maven.enforcer.rule.RequireEncoding">
                <!-- Validate files against this encoding -->
                <encoding>UTF-8</encoding>
                <!-- Comma-separated globs of files to be validated -->
                <includes>src/**/*.java,**/*.xml</includes>
                <!-- Comma-separated globs of files to be excluded from validation -->
                <excludes>target/**</excludes>
                <!-- Enables SCM files exclusions -->
                <useDefaultExcludes>true</useDefaultExcludes>
              </requireEncoding>
            </rules>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```
