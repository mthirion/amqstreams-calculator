APP_DIR=/development/workspace/internal/projects/amqstreams-calculator

cd $APP_DIR

if [[ $1 == "start" ]]
then
	echo "starting backend..."
	cd calculator-backend
	mvn quarkus:dev &
	sleep 2
	BCK_PID=$(ps -ef | grep calculator-backend | grep quarkus:dev | awk '{print $2}')
	cd ..

	echo "starting frontend..."
	cd calculator-ui
	npm start &
	sleep 2
	FRT_PID=$(ps -ef | grep calculator-ui | grep 'react-scripts start' | awk '{print $2}')
	cd ..
fi

if [[ $1 == "stop" ]]
then
	echo "stopping services..."
	kill $BCK_PID 2>/dev/null
	kill $FRT_PID 2>/dev/null
fi
