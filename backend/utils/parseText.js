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

  const tokenizer = new natural.WordTokenizer();
  const tokens = tokenizer.tokenize(modifiedText);

  const ruleSet = new natural.RuleSet("EN");
  const lexicon = new natural.Lexicon("EN", "CD");

  const nounInflector = new natural.NounInflector();
  const posTagger = new natural.BrillPOSTagger(lexicon, ruleSet);

  nounTags = ["NN", "NNS", "NNP", "NNPS"];

  const taggedTokens = posTagger.tag(tokens);
  let quantity, item;

  for (let taggedToken of taggedTokens.taggedWords) {
    if (taggedToken.tag === "CD") {
      quantity = parseInt(taggedToken.token);
      if (isNaN(quantity)) {
        quantity = wordsToNumbers.wordsToNumbers(taggedToken.token);
      }
    } else if (nounTags.includes(taggedToken.tag)) {
      item = nounInflector.singularize(taggedToken.token);
    }
  }
  const dict = { item, quantity };
  console.log({ text, dict });
  return dict;
};
