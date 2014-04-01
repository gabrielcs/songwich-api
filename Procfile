web: target/start -Dhttp.port=${PORT} {JAVA_OPTS}
scheduledping: java -Dconfig.file=conf/application.conf -cp "target/staged/*" jobs.PingerJob .