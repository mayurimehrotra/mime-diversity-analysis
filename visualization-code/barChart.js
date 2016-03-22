var fileType = null;
function myFunction() {
        fileType = document.getElementById("selectFileType").value;
            console.log(fileType);
        $("#tab1").html("");
        $("#tab2").html("");
        var path = "BFAFingerPrint/"+fileType+'.tsv';
        drawFPBarChart("tab1",path);
    
        var path = "BFACorrScore/"+fileType+'.tsv';
        drawBarChart("tab2",path,1);
        }

function drawFPBarChart(tabId,path,minMaxFlag){
    var margin = {top: 70, right: 20, bottom: 30, left: 250},
    width = 1170 - margin.left - margin.right,
    height = 530 - margin.top - margin.bottom;

    var formatPercent = d3.format(".0%");

    var x = d3.scale.ordinal()
        .rangeRoundBands([0, width], 0.5);

    var y = d3.scale.linear()
        .range([height, 0]);

    var xAxis = d3.svg.axis()
        //.scale(x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left")
        .tickFormat(formatPercent);

    var tip = d3.tip()
      .attr('class', 'd3-tip')
      .offset([-10, 0])
      .html(function(d) {
    return "<strong>Byte: <span style='color:red'>"+ d.letter+"</span> Frequency: </strong> <span style='color:red'>" + d.frequency + "</span>";
  })
      
    var tipArr = [];

    var svg = d3.select("#"+tabId).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
      .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    svg.call(tip);

    d3.tsv(path, type, function(error, data) {
      console.log(data[4].letter+' '+data[4].frequency);
      var max1=0,max2=0;
      var min1=1,min2=1;
      for(i=0;i<data.length;i++){
          if(data[i].frequency > max1){
              max2 = max1;
              max1= data[i].frequency;
          }else if(data[i].frequency > max2){
              max2 = data[i].frequency;
          }
          if(data[i].frequency<min1){
              if(data[i].frequency < min1){
                  min2 = min1;
                  min1 = data[i].frequency;
              }else if(data[i].frequency < min2){
                  min2 = data[i].frequency
              }
          }
      }
//      console.log(max1+' '+max2);
//      console.log(min1+' '+min2);
    
      x.domain(data.map(function(d) { return d.letter; }));
      y.domain([0, 1]);//d3.max(data, function(d) { return d.frequency; })]);

      svg.append("g")
          .attr("class", "x axis")
          .attr("transform", "translate(0," + height + ")")
          .call(xAxis);

      svg.append("text")
            .attr("transform", "translate(" + (width / 2) + " ," + (height + margin.bottom) + ")")
            //.attr("dy", ".5em")
            .style("text-anchor", "middle")
            .text("Byte Value");

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
          .on('mouseover', tip.show)
          .on('mouseout', tip.hide)

    });

    function type(d) {
      d.frequency = +d.frequency;
      return d;
    }
}//end of drawBarChart

    

function drawBarChart(tabId,path,minMaxFlag){
    var margin = {top: 20, right: 20, bottom: 30, left: 250},
    width = 1170 - margin.left - margin.right,
    height = 530 - margin.top - margin.bottom;

    var formatPercent = d3.format(".0%");

    var x = d3.scale.ordinal()
        .rangeRoundBands([0, width], 0.5);

    var y = d3.scale.linear()
        .range([height, 0]);

    var xAxis = d3.svg.axis()
        //.scale(x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left")
        .tickFormat(formatPercent);

    var tip = d3.tip()
      .attr('class', 'd3-tip')
      .offset([-10, 0])
      
    var tipArr = [];

    var svg = d3.select("#"+tabId).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
      .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    svg.call(tip);

    d3.tsv(path, type, function(error, data) {
      console.log(data[4].letter+' '+data[4].frequency);
      var max1=0,max2=0;
      var min1=1,min2=1;
      for(i=0;i<data.length;i++){
          if(data[i].frequency > max1){
              max2 = max1;
              max1= data[i].frequency;
          }else if(data[i].frequency > max2){
              max2 = data[i].frequency;
          }
          
              if(data[i].frequency < min1){
                  min2 = min1;
                  min1 = data[i].frequency;
              }else if(data[i].frequency < min2){
                  min2 = data[i].frequency
              }
          
      }
//      console.log(max1+' '+max2);
      console.log(min1+' '+min2);
    
      x.domain(data.map(function(d) { return d.letter; }));
      y.domain([0, 1]);//d3.max(data, function(d) { return d.frequency; })]);

      svg.append("g")
          .attr("class", "x axis")
          .attr("transform", "translate(0," + height + ")")
          .call(xAxis);

      svg.append("text")
            .attr("transform", "translate(" + (width / 2) + " ," + (height + margin.bottom) + ")")
            //.attr("dy", ".5em")
            .style("text-anchor", "middle")
            .text("Byte Value");

      svg.append("g")
          .attr("class", "y axis")
          .call(yAxis)
        .append("text")
          .attr("transform", "rotate(-90)")
          .attr("y", 6)
          .attr("dy", ".71em")
          .style("text-anchor", "end")
          .text("Frequency");
      
        for(i = 0 ; i < data.length ; i++){
            if((data[i].frequency == max1 || data[i].frequency == max2) && minMaxFlag == 1){
                
                svg.append("rect")
                  .attr("class", "maxBar")
                  .attr("x", x(data[i].letter))
                  .attr("width", x.rangeBand())
                  .attr("y", y(data[i].frequency))
                  .attr("height", height - y(data[i].frequency))
            }else if((data[i].frequency == min1 || data[i].frequency == min2) && minMaxFlag==1){
                svg.append("rect")
                  .attr("class", "minBar")
                  .attr("x", x(data[i].letter))
                  .attr("width", x.rangeBand())
                  .attr("y", y(data[i].frequency))
                  .attr("height", height - y(data[i].frequency))
            }else{
                svg.append("rect")
                  .attr("class", "bar")
                  .attr("x", x(data[i].letter))
                  .attr("width", x.rangeBand())
                  .attr("y", y(data[i].frequency))
                  .attr("height", height - y(data[i].frequency))
            }
          
      } 
    });

    function type(d) {
      d.frequency = +d.frequency;
      return d;
    }
}//end of drawBarChart
