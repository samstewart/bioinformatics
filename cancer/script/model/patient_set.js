/** Simple container for set of patients*/
function PatientSet(learning_type) {
    this.learning_type = learning_type;
    this.type = 'PatientSet';
    this.patients = [];
}

PatientSet.prototype.addPatient = function(patient) {
    patient.learning_type = this.learning_type;
    this.patients.push(patient);
}

PatientSet.prototype.getPatient = function(index) {
    return this.patients[index];
}