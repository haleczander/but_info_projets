$CLASSES = 'classes'
$LIB = 'lib'
$RESSOURCES = 'ressources'

$CLASSPATH = "$((Get-Childitem -Path $LIB -Filter '*.jar' -Recurse -File).fullname -join ';');."

# Copy-Item -Path $RESSOURCES/* -Destination $CLASSES -Recurse
cd $CLASSES
java -cp $CLASSPATH $args
cd ..