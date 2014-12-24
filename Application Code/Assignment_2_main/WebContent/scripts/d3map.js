function main() {
	
	 var table_name = $("#menu").val();
	 getTweets( table_name);
	 
	 
	 
	 $("#menu" ).change(function () {

			table_name = $("#menu").val();
			$("svg").remove();
			 getTweets( table_name);
			
		});
}


function getTweets( table_name) {
	
	//var serverUrl = "http://localhost:8080/Assignment_1_different/index.jsp?tablename="+table_name;
	var serverUrl = "/index.jsp?tablename="+table_name;

	
	$.ajax({
	    url : serverUrl,
	    data : '',
	    dataType: 'json',
	    success : function(jsonarray) {
	        
	        var heatmapData = [];
	        var textData=[];
	        var scoreData = [];
	        var lat_arr=[];
	        var lng_arr=[];
	        
	        $.each(jsonarray, function(i, tweet) {
	        	var lng = tweet.lng;
	        	var lat = tweet.lat;
	        	var text= tweet.text;
	        	//alert(tweet.score);
	        	var t_score=tweet.score.split(',');
	        	var n_score=t_score[1];
	        	var m_score=t_score[0];
	        	//var point = new google.maps.LatLng(lat,lng);
	        	//heatmapData[heatmapData.length] = point;
	        	scoreData[scoreData.length] = n_score;
	        	textData[textData.length]= text;
	        	
	        	lat_arr[lat_arr.length] = lat;
	        	lng_arr[lng_arr.length] = lng;
	        });
	        
	        //alert(scoreData.length);
	        //alert(textData);
	        d3map (scoreData);
	    }
	});
	
}

function d3map (scoreData){
	var margin = {top: 20, right: 20, bottom: 30, left: 40},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var formatPercent = d3.format(".0%");

var x = d3.scale.ordinal()
    .rangeRoundBands([0, width], .1, 1);

var y = d3.scale.linear()
    .range([height, 0]);

var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .tickFormat(formatPercent);

var svg = d3.select("body").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var frequency =[0,0,0,0,0];
for(i=0;i<scoreData.length;i++){
	if (scoreData[i] == -9999){
		scoreData[i] = 0;
	}
	if(parseFloat(scoreData[i]) < -0.6){
		frequency[0]++;
	}
	else if (parseFloat(scoreData[i]) < -0.2){
		frequency[1]++;
	}
	else if (parseFloat(scoreData[i]) < 0.2){
		frequency[2]++;
	}
	else if (parseFloat(scoreData[i]) < 0.6){
		frequency[3]++;
	}
	else {
		frequency[4]++;
	}
}
//alert(frequency[0]+","+frequency[1]+","+frequency[2]+","+frequency[3]+","+frequency[4] );
sum = frequency[0]+frequency[1]+frequency[2]+frequency[3]+frequency[4];
var pfrequency = [0,0,0,0,0];
for (i=0; i<frequency.length; i++){
	pfrequency[i] = frequency[i]/sum;
}

//alert(pfrequency[0]+","+pfrequency[1]+","+pfrequency[2]+","+pfrequency[3]+","+pfrequency[4] );

d1=[{"frequency": pfrequency[0], "letter":"1. Adverse", "color":"Maroon"}, {"frequency": pfrequency[1], "letter":"2. Unfavorable", "color":"OrangeRed"}, {"frequency": pfrequency[2], "letter":"3. Neutral", "color":"Gray"}, {"frequency": pfrequency[3], "letter":"4. Favorable", "color":"PaleGreen"}, {"frequency": pfrequency[4], "letter":"5. Supportive", "color":"SeaGreen"}];

//d1=[{"frequency": "0.1", "letter": "A"},{"frequency": "0.6", "letter": "B"},{"frequency": "0.3", "letter": "C"}];
//alert(d1[0].frequency);
d3.tsv("data.tsv",function(error, data) {
//alert(data[0].frequency);
data=d1;
  data.forEach(function(d) {
    d.frequency = +d.frequency;
  });

  x.domain(data.map(function(d) { return d.letter; }));
  y.domain([0, d3.max(data, function(d) { return d.frequency; })]);

  svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis)
      .append("text")
      .attr("x", width)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Sentiment Distribution");

  svg.append("g")
      .attr("class", "y axis")
      .call(yAxis)
    .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Frequency");

  svg.selectAll(".bar")
      .data(data)
    .enter().append("rect")
      .attr("class", "bar")
      .attr("x", function(d) { return x(d.letter); })
      .attr("width", x.rangeBand())
      .attr("y", function(d) { return y(d.frequency); })
      .attr("height", function(d) { return height - y(d.frequency); })
      .style("fill", function(d) { return d.color; });

  d3.select("input").on("change", change);

  var sortTimeout = setTimeout(function() {
    d3.select("input").property("checked", true).each(change);
  }, 2000);

  function change() {
    clearTimeout(sortTimeout);

    var x0 = x.domain(data.sort(this.checked
        ? function(a, b) { return b.frequency - a.frequency; }
        : function(a, b) { return d3.ascending(a.letter, b.letter); })
        .map(function(d) { return d.letter; }))
        .copy();

    var transition = svg.transition().duration(750),
        delay = function(d, i) { return i * 50; };

    transition.selectAll(".bar")
        .delay(delay)
        .attr("x", function(d) { return x0(d.letter); });

    transition.select(".x.axis")
        .call(xAxis)
      .selectAll("g")
        .delay(delay);
  }
});

	

}

$(document).ready(main);