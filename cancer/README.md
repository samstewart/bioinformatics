### Subtle Issues
We need recursive conversion of the prototype when importing data. Still need a better ORM. That will lead to some subtle bugs if I'm not careful.

We need to convert the .p_index to a zero based index very carefully.


We should definitely have some sort of ORM for mapping between the database and actual javascript types. We need a way to "deep" conversion.

It's not that clean that we keep the patients and their genes separate due to the type conversion issues.


We also have a problem with replicating the diseases. We insert once for the training and once for the testing. Right now we are hard coding everything into predictor.js but this is non-optimal. We should be deducing them from the database.

I would also like to add a javascript worker somewhere to this task to avoid freezing the browser.

I think I might be adding one more predictive gene than necessary?