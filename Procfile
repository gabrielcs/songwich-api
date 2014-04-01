web: target/start -Dhttp.port=${PORT} {JAVA_OPTS}
scheduledping: java -Dconfig.file=conf/application.conf -cp "target/universal/stage/lib/*" jobs.PingerJob .