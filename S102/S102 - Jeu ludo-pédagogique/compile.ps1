$SOURCES = 'src'
$CLASSES = 'classes'
$LIB = 'lib'
$RESSOURCES = 'ressources'

$CLASSPATH = "$((Get-Childitem -Path $LIB -Filter '*.jar' -Recurse -File).fullname -join ';');."
$FILESPATH = (Get-Childitem -Path $SOURCES -Filter '*.java' -Recurse -File).fullname

javac -cp $CLASSPATH -sourcepath $SOURCES -d $CLASSES $FILESPATH

Copy-Item -Path $RESSOURCES/* -Destination $CLASSES -Recurse -force