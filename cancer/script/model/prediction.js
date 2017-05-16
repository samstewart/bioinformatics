function Prediction(disease, score, patient, classAVotes, classBVotes) {
    this.patient            = patient;
    this.score              = score;
    this.disease            = disease;
    this.SURITY_THRESHOLD   = .3;
    this.classAVotes        = classAVotes;
    this.classBVotes        = classBVotes;
}

Prediction.prototype.getScore = function() {
    return this.score;
}

Prediction.prototype.isUnsure = function() {
    return this.score < this.SURITY_THRESHOLD;
}

Prediction.prototype.getDiseaseName = function() {
    return this.disease.name;
}

Prediction.prototype.isCorrect = function() {
    return (this.disease.name == this.patient.disease);
}
Prediction.prototype.getFancyString = function() {
    var diagnosis = null;
    if (this.isUnsure()) {
        diagnosis = 'unsure';
    } else {
        diagnosis = this.isCorrect() ? 'accurate' : 'inaccurate';
    }
    
    return this.patient.p_index + ': ' + this.disease.name + ' score: ' + this.score + ' correct: ' + diagnosis + ' true disease: ' + this.disease.name;
}