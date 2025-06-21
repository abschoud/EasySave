// src/routes/analysis.js

import dotenv from 'dotenv'; // Import dotenv
dotenv.config(); // Load environment variables in this module too

import express from 'express';
import { GoogleGenAI } from '@google/genai'; // Use import for the class

const apiKey = process.env.GOOGLE_API_KEY;
console.log("Google API Key:", apiKey); // Add this line to check the value

// Initialize the Google Generative AI client
// Use the correct constructor signature with an object { apiKey: ... }
const genAI = new GoogleGenAI({ apiKey: apiKey });

const router = express.Router(); // Create an Express Router

// Define the POST endpoint for analysis
router.post('/', async (req, res) => {
  // Extract both spendingData and budgets from the request body
  const { spendingData, budgetData } = req.body;

  // Get the model name from the query parameter
  const modelName = req.query.model;
  console.log("Received model name:", modelName); // Log the received model name

  if (!spendingData || !Array.isArray(spendingData) || spendingData.length === 0) {
    return res.status(400).json({ error: 'Invalid or empty spending data provided.' });
  }

  // Add a check for the modelName
  if (!modelName) {
      return res.status(400).json({ error: 'Model name is required as a query parameter.' });
  }

  // Format the financial data for the AI prompt, including date and type
  const formattedFinancialData = spendingData.map(entry => {
      const date = new Date(entry.date).toLocaleDateString(); // Format date for readability
      return `${entry.type} - ${entry.category}: ${entry.amount} on ${date}`;
    }).join('\n');

  // Format the budget data for the AI prompt
  const formattedBudgetData = budgetData.map(budget => {
      return `Budget ${budget.category} (${budget.period}): Limit ${budget.limit}, Spent ${budget.spent}, Start Date ${budget.startDate}, End Date ${budget.endDate}`;
    }).join('\n');

    // Craft a more detailed prompt, including budget information and refined instructions
  const prompt = `Analyze the following financial data (spending and income) and budget information.

Data:
${formattedFinancialData}
${formattedBudgetData}

Instructions:
- Analyze the provided financial transactions to identify spending patterns and trends over the period covered by the data.
- Highlight categories with the highest spending and any categories where spending appears to be increasing or particularly frequent.
- Provide a brief overview of the financial flow, noting the relationship between income and outgoing transactions.
- Analyze the provided budget information in relation to the spending data.
- For each budget listed, comment on the current status (e.g., on track, close to limit, over budget) based on the 'Spent' and 'Remaining' values compared to the 'Limit'.
- Specifically for the 'Savings Goal' budget, comment on the progress made towards the 'Limit' and suggest actionable steps to help reach the goal, drawing insights from the spending and income data.
- Offer personalized and practical suggestions for improving financial habits, focusing on areas identified in the analysis (e.g., high spending categories, budgets close to or over limit).
- Ensure the response is concise, easy to understand, and provides clear takeaways and actionable advice.
- Avoid making assumptions about specific transaction details beyond the provided data points (category, amount, date, type).
- Make sure the formatting is neat and tidy. Use dashes instead of asterisk to bullet point.
- Make sure you are consistent with the spacing between headers and the information it lists. Headers should have only a single line space above and below it every time.
- For headers, only use bolding for making it apparent. Do not use any numbering to list.
- Do not use any hashtag symbols, but you must bold headers and titles.
- Note that all spending is done in pounds, not dollars, so make sure to only use the £ symbol when referring to specific spending.

Analysis:
`;

  try {
    // Use the new method based on the documentation: ai.models.generateContent
    const result = await genAI.models.generateContent({
      model: modelName, // Use the modelName received from the query parameter
      contents: prompt, // Pass the prompt as contents
    });

    // Access the text directly from the result object
    const text = result.text; // Changed from result.response.text()

    console.log ("Sent Prompt :", prompt)
    console.log("Generated Text :", text); // Log the generated text

    res.json({ analysis: text }); // Send the AI analysis back
  } catch (error) {
    console.error('Error generating AI analysis:', error);
    res.status(500).json({ error: 'Failed to generate AI analysis.' });
  }
});

export default router; // Export the router using default export