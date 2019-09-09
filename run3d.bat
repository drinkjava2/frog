cd core3d
call mvn clean compile
cd target\classes
java -classpath ".;*" com.github.drinkjava2.frog.Application
@pause