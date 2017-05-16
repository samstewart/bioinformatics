/** Simple library for doing visualizations of our predictions.*/


function Visualization() {
    
}

// Code for doing color conversions
// courtesy of stackoverflow.
function componentToHex(c) {
    var hex = Math.floor(c * 255).toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(rgb) {
    return "#" + componentToHex(rgb['r']) + componentToHex(rgb['g']) + componentToHex(rgb['b']);
}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16) / 255,
        g: parseInt(result[2], 16) / 255,
        b: parseInt(result[3], 16) / 255
    } : null;
}

/** Utility function for doing weighted color predicitons*/
function weightedPredictionScoreColor(c1, c2, votesA, votesB) {
    // we normalize the weight according to some magic value. We want the number between -1 and 1.
    rgb1 = hexToRgb(c1);
    rgb2 = hexToRgb(c2);
    
    // find average point then deviate according to score which is always between -1 and 1
    avg = {r: rgb1['r'] + rgb2['r'], g: rgb1['g'] + rgb2['g'], b: rgb1['b'] + rgb2['b']};
    
    avg['r'] /= 2;
    avg['g'] /= 2;
    avg['b'] /= 2;
    
    score = votesA - votesB;
    score /= votesA + votesB;
    
    // now deviate according to weight
    avg['r'] = avg['r'] + avg['r'] * score;
    avg['g'] = avg['g'] + avg['g'] * score;
    avg['b'] = avg['b'] + avg['b'] * score;
    
    return rgbToHex(avg);
}

/** Weights base color by number of votes we have. Controls intensity of color. 
You should pass in a "full saturation" color and we will step back down according to number of votes.*/
function weightedVoteColor(baseC, votes) {
    NORMALIZE_VOTE = 50;
    
    votes /= NORMALIZE_VOTE; // we want the value between 0 and 1
    
    rgb = hexToRgb(baseC);
    
    // the more sure you are the closer you are to the true color
    rgb['r'] = Math.max(0, rgb['r'] - rgb['r'] * (1 - votes) * .50);
    rgb['g'] = Math.max(0, rgb['g'] - rgb['g'] * (1 - votes) * .50);
    rgb['b'] = Math.max(0, rgb['b'] - rgb['b'] * (1 - votes) * .50);
    
    return rgbToHex(rgb);
}

/** Draws our visualization to the given canvas.*/
Visualization.prototype.visualize = function(predictor, predictions, canvas) {

    var barHeight = 20;
    var barSpacing = 10;
    var textHeight = 30;
    var ballRadius = 10;
    var ballSpacing = 10;

    canvas.height = predictions.length * (barHeight + barSpacing) + textHeight;
    
    var context = canvas.getContext('2d');
    var box_width = (canvas.width - ballSpacing * 2 - ballRadius * 2) / 2.0;
    
    // draw the text for the two classes
    context.fillStyle = "black";
    context.font = "bold " + textHeight +"px Arial";
    
    context.fillText("AML", box_width / 2.0 - context.measureText('AML').width / 2.0, textHeight);
    context.fillText("ALL", box_width + ballSpacing * 2 + ballRadius * 2 + box_width / 2.0 - context.measureText('ALL').width / 2.0, textHeight);

    var colA = '#7D00FA';
    var colB = '#FA0064';
    
    // now we draw all the rectangles for each class
    predictions.forEach(function(pred, index, arry) {
        context.fillStyle = weightedVoteColor(colA, pred.classAVotes);
        
        var y =  textHeight + barSpacing + index * (barHeight + barSpacing);
        
        context.fillRect(0, y, box_width, barHeight);
        
        context.fillStyle = weightedVoteColor(colB, pred.classBVotes);
        
        context.fillRect(box_width + ballSpacing * 2 + ballRadius * 2, y, box_width, barHeight);
        
        context.fillStyle = weightedPredictionScoreColor(colA, colB, pred.classAVotes, pred.classBVotes);
        context.beginPath();
        
        context.arc(box_width + ballSpacing + ballRadius,  y + ballRadius, ballRadius, 0, Math.PI * 2.0, true);
        
        context.closePath();
        
        context.fill();
    });
}