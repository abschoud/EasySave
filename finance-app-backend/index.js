// src/index.js

    import dotenv from 'dotenv';
    dotenv.config(); // Load environment variables first

    import admin from 'firebase-admin';
    import express from 'express';
    import { GoogleGenAI } from '@google/genai'; // Use import for the class
    import analysisRoutes from './src/routes/analysis.js'; // Import the router (keep .js extension for now)
    import https from 'https'; // Import the https module
    import http from 'http'; // Import the http module
    import fs from 'fs'; // Import the file system module

    // Initialize Firebase Admin SDK (might also depend on GOOGLE_APPLICATION_CREDENTIALS)
    admin.initializeApp({
      credential: admin.credential.applicationDefault(),
      // Add other Firebase project configuration if needed
    });

    console.log('Firebase Admin SDK initialized.');

    const app = express();
    const port = process.env.PORT || 3000;

    // Middleware to parse JSON request bodies
    app.use(express.json());

    // Middleware to verify Firebase Auth ID tokens (optional)
    const verifyFirebaseToken = async (req, res, next) => {
      // ... implementation ...
      next(); // For now, just proceed
    };

    // Mount your analysis routes
    app.use('/api/analysis', analysisRoutes);

    // Basic route for testing
    app.get('/', (req, res) => {
      res.send('Finance App Backend is running!');
    });

    // --- HTTPS Configuration ---
    const options = {
      key: fs.readFileSync('./src/srv.key'), // Replace with your key path
      cert: fs.readFileSync('./src/srv.crt'), // Replace with your certificate path
      ca: fs.readFileSync('./src/ca.crt')
      // If using a self-signed certificate, you might need ca: fs.readFileSync('path/to/your/ca.crt')
    };

    // Start the HTTPS server
    //https.createServer(options, app).listen(port, () => {
    //  console.log(`HTTPS Server is running on port ${port}`);
    //});

    // Start the HTTP server
    http.createServer(app).listen(port, () => {
        console.log(`HTTP Server is running on port ${port}`);
    });    