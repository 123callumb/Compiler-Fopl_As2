function testFunction(num1, num2){
	console.log("This variable is greater than 2");
	var test  = "lol";
	if(num1>2.5){
		console.log("If has returned true");
	}
	else if(4.5>num2){
		console.log("This is else if bit");
	}
	else {
		console.log("This is else bit");
	}
	for(var i= 0.0;i<10.0;i++){
		console.log("This is a for loop");
	}
}
function functionTwo(){
	var num_1  = 1.2;
	var num_2  = 3.4;
	testFunction(num_1, num_2);
	while(num_1<num_2){
		console.log("it is still smaller");
	}
	var floatArray  = [2.3,4.5,6.9];
	for(element in floatArray){
		console.log("This is a for Each statement");
	}
}
function Main(){
	console.log("Hello World");
	var hello  = "Hellow";
	const num  = 6.9;
	return num;
}
Main();
