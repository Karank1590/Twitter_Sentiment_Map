var heatmap; 
var gmarkers = [];



function main() {
	
	 if(sessionStorage.getItem("map_option")==null)
		 {
		 var table_name = $("#menu").val();
		 var map_option = $("#map_option").val();
		 sessionStorage.setItem("table_name",table_name);
		 sessionStorage.setItem("map_option",map_option);
		 }
	 else
		 {
		 var map_option=sessionStorage.getItem("map_option");
		 var table_name=sessionStorage.getItem("table_name");
		 $("#map_option").val(map_option);
		 $("#menu").val(table_name);
		 }
	
	//var map = initialize();
	getTweets(table_name,map_option);
	

	
	$("#map_option" ).change(function () {
		map_option = $("#map_option").val();
		table_name = $("#menu").val();
		sessionStorage.setItem("table_name",table_name);
		sessionStorage.setItem("map_option",map_option);
		
		getTweets(table_name, map_option);
	});
	
	$("#menu" ).change(function () {
		map_option = $("#map_option").val();
		table_name = $("#menu").val();
		sessionStorage.setItem("table_name",table_name);
		sessionStorage.setItem("map_option",map_option);
		
		getTweets(table_name, map_option);
	});
	
}

function getTweets(table_name, map_option) {
	
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
	        	var point = new google.maps.LatLng(lat,lng);
	        	heatmapData[heatmapData.length] = point;
	        	scoreData[scoreData.length] = n_score;
	        	textData[textData.length]= text;
	        	
	        	lat_arr[lat_arr.length] = lat;
	        	lng_arr[lng_arr.length] = lng;
	        });
	        
	        //alert(heatmapData.length);
	        //alert(textData);
	        $('#tst').val(heatmapData.length);
	        if(map_option=="false")
	        {
	        	//createHeatmap(map, heatmapData,scoreData);
	        	//alert("hii");
	        	heatmap_js(lat_arr,lng_arr,scoreData);
	        }
	        else
	        {
	        	coordinates(heatmapData,textData,scoreData);
	        }	
	    }
	});
	
}

function initialize() {
	var center_loc = new google.maps.LatLng(25.6586, -80.3568);

    var mapOptions = {
      center: center_loc,
      zoom: 2
    };
    //alert($("#map-canvas"));
    var map = new google.maps.Map($("#map-canvas")[0], mapOptions);
    return map;
}

function removeMarkers(){
    for(i=0; i<gmarkers.length; i++){
        gmarkers[i].setMap(null);
    }
}


function coordinates(heatmapData,textData,scoreData)
{
	
	if(heatmap) {
		heatmap.setMap(null);
	}
	
	//heatmap.setMap(null);
	removeMarkers();
	map = initialize();
	
	
	
	var infowindow = new google.maps.InfoWindow();
	
	
	for (i = 0; i < heatmapData.length; i++) 
  	{
		var fill_c="";
		var stroke_c="";
			
		 if(scoreData[i]=="-9999" ||scoreData[i]=='0' )
	    	{
			 // Not parsed
			 fill_c='#0000FF';
			 stroke_c='#0000FF';
	    	}
		 else if(parseFloat(scoreData[i])>0)
	    	{
			 // Positvie
			 fill_c='#00FF00';
			 stroke_c='#00FF00';
	    	}
		 else if(parseFloat(scoreData[i])<0)
	    	{
			 // Red
			 fill_c='#FF0000';
			 stroke_c='#FF0000';
	    	}
		
	var marker = new google.maps.Marker({
	    position: heatmapData[i],
	    title: textData[i],
	    map:map,
	    
	   
	    
	    icon: {

	        path: google.maps.SymbolPath.CIRCLE,

	            fillOpacity: 0.5,

	            fillColor: fill_c,

	           strokeOpacity: 0.0,

	           strokeColor: stroke_c,

	           strokeWeight: 0.0, 

	           scale: 4 //pixelsoogle.maps.SymbolPath.CIRCLE,
	           }
	    	
	});

	
	 

	
	google.maps.event.addListener(marker, 'click', function() {
		infowindow.setContent(this.title);
        infowindow.open(map, this);
	  });
	
	
	
	// Push your newly created marker into the array:
	gmarkers.push(marker);
  	}
	
	
	
	var layer = new google.maps.FusionTablesLayer({
        query: {
          select: 'Location',
         // from: '1NIVOZxrr-uoXhpWSQH2YJzY5aWhkRZW0bWhfZw'
        },
        map: map
      });
	map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].pop(legend);
      // Create the legend and display on the map
      var legend = document.createElement('div');
      legend.id = 'legend';
      var content = [];
      content.push('<h3>Legend</h3>');
      content.push('<p><div class="color red"></div>Negative Sentiment</p>');
      content.push('<p><div class="color green"></div>Positive Sentiment</p>');
      content.push('<p><div class="color blue"></div>Neutral Sentiment</p>');
      legend.innerHTML = content.join('');
      legend.index = 1;
      map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend);
	
}
function convertRange( value, r1, r2 ) { 
    return ( value - r1[ 0 ] ) * ( r2[ 1 ] - r2[ 0 ] ) / ( r1[ 1 ] - r1[ 0 ] ) + r2[ 0 ];
}

/*
function createHeatmap(heatmapData,scoreData) {
	removeMarkers();
	
	if(heatmap) {
		heatmap.setMap(null);
	}
	
	var heatmapData1 = [];
	for (i = 0; i < heatmapData.length; i++) 
  	{	
		
		heatmapData1[heatmapData1.length] = {location: heatmapData[i], weight: Math.pow(convertRange(scoreData[i], [-1, 1], [1, 3]),2)};
  	}
	
	heatmap = new google.maps.visualization.HeatmapLayer({
	  	data: heatmapData1,
	  	//radius: 25
	});
	
	map=initialize();
	heatmap.setMap(map);
}

*/

function heatmap_js(lat_arr,lng_arr,scoreData){
	
	   var myLatlng = new google.maps.LatLng(25.6586, -80.3568);
       // map options,
       var myOptions = {
         zoom: 2,
         center: myLatlng
      };
       // standard map
	
	
	
	
	
       map = new google.maps.Map(document.getElementById("map-canvas"), myOptions);
       // heatmap layer
	
	
	
	
       heatmap = new HeatmapOverlay(map, 
         {
           // radius should be small ONLY if scaleRadius is true (or small radius is intended)
           "radius":1.5,
           "maxOpacity": 1, 
           
           // scales the radius based on map zoom
           "scaleRadius": true, 
           // if set to false the heatmap uses the global maximum for colorization
           // if activated: uses the data maximum within the current map boundaries 
           //   (there will always be a red spot with useLocalExtremas true)
           "useLocalExtrema": true,
           "blur" : 1,
           gradient: {
               // enter n keys between 0 and 1 here
               // for gradient color customization
               '.3': '#FF0000',
               '.5': '#0000FF',
               '0.9': '#00FF00'
             },
           // which field name in your data represents the latitude - default "lat"
           latField: 'lat',
           // which field name in your data represents the longitude - default "lng"
           lngField: 'lng',
           // which field name in your data represents the data value - default "value"
           valueField: 'count'
         }
       );

       var data1 =[];
    		
       for(i=0;i<lat_arr.length;i++)
       {
    	   if (scoreData[i]==-9999)
    		   {
    		   temp=0;
    		   }
    	   else
    		   {
    		   temp=scoreData[i];
    		   }
    	   data1.push({ 
    	        "lat" : lat_arr[i],
    	        "lng"  : lng_arr[i],
    	        "count"       : convertRange(temp, [-1, 1], [0, 1])
    	    });
    	}
       
       
       var testData = {
         max: lat_arr.length,
         data: data1
       };

       heatmap.setData(testData);
	
}



$(document).ready(main);