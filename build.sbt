scalaVersion := "2.13.18"

lazy val root = (project in file("."))
  .settings(
    name := "SimpleTransformerJsonXmlZIO",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"           % "2.1.26",
      "dev.zio" %% "zio-http"      % "3.11.3",
      "dev.zio" %% "zio-json"      % "0.9.2",

      "com.typesafe" % "config"    % "1.4.9",

      "com.typesafe.slick" %% "slick"         		  	 % "3.6.1",
      "com.typesafe.slick" %% "slick-hikaricp"	       % "3.6.1",
      "com.h2database"      % "h2"          			  	 % "2.4.240",

      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml"      	% "2.22.1",
      "com.fasterxml.jackson.module"    %% "jackson-module-scala"        	% "2.22.1",

      "ch.qos.logback"          	% "logback-classic"            					% "1.5.38",
      "net.logstash.logback"      % "logstash-logback-encoder"   	      	% "9.0",
      "dev.zio"            				%% "zio-logging"              					% "2.5.3",
      "dev.zio"              			%% "zio-logging-slf4j"       			  		% "2.5.3"
    )

    /*scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked"
    )*/
  )
