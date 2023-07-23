const natural = require("natural");
const wordsToNumbers = require("words-to-numbers");

exports.getQueryDict = (text) => {
  // parse text
  let modifiedText = text.replace(
    /\b(a|one|two|three|four|five|six|seven|eight|nine) (hundred|thousand|million|billion)\b/gi,
    (match) => {
      return wordsToNumbers.wordsToNumbers(match);
    }
  );
  modifiedText = modifiedText.replace(/\b(a|an)\b/gi, "one");

  var classifier = new natural.BayesClassifier();
  classifier.addDocument("Add 50 apples", "add");
  classifier.addDocument("Collected ten apples", "add");
  classifier.addDocument("Picked up fifty apples", "add");
  classifier.addDocument("remove 100 apples", "remove");
  classifier.addDocument("remove 100 blueberries", "remove");
  classifier.addDocument("I sold eleven apples", "remove");
  classifier.addDocument("gave away seventeen apples", "remove");
  classifier.addDocument("donate 55 apples", "remove");
  classifier.train();

  const tokenizer = new natural.WordTokenizer();
  const tokens = tokenizer.tokenize(modifiedText);

  const ruleSet = new natural.RuleSet("EN");
  const lexicon = new natural.Lexicon("EN", "CD");

  const nounInflector = new natural.NounInflector();
  const posTagger = new natural.BrillPOSTagger(lexicon, ruleSet);

  nounTags = ["NN", "NNS", "NNP", "NNPS"];

  const taggedTokens = posTagger.tag(tokens);
  let quantity = 0;
  let item;

  for (let taggedToken of taggedTokens.taggedWords) {
    if (taggedToken.tag === "CD") {
      tempQuantity = parseInt(taggedToken.token);
      if (isNaN(tempQuantity)) {
          quantity += wordsToNumbers.wordsToNumbers(taggedToken.token)
      } else {
          quantity += tempQuantity
      }
    } else if (nounTags.includes(taggedToken.tag)) {
      item = nounInflector.singularize(taggedToken.token);
    }
  }
  const dict = {"action":classifier.classify(modifiedText), dict:{'item':item, 'quantity': quantity}}
  return dict
};