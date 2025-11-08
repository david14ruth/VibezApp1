require('dotenv').config();

// Firebase admin setup
const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

// Supabase setup
const { createClient } = require('@supabase/supabase-js');

// Hardcoded Supabase values
const SUPABASE_URL = 'https://lueirepdjoxpbziluboi.supabase.co';
const SUPABASE_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx1ZWlyZXBkam94cGJ6aWx1Ym9pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTc1OTI4MjAsImV4cCI6MjA3MzE2ODgyMH0.fEywGdJneKcet5t4Pp01XXI-k9h3gukuRAjJWPXoEwQ'; // your service_role key

const supabase = createClient(SUPABASE_URL, SUPABASE_KEY);

(async () => {
  const mySubscription = supabase
    .channel('public:song') // use "public" schema
    .on(
      'postgres_changes',
      {
        event: 'INSERT',
        schema: 'public',
        table: 'song',
      },
      async (payload) => {
        console.log('New song added:', payload.new);

        // Get all device tokens
        const { data: tokensData, error } = await supabase
          .from('fcm_tokens')
          .select('token');

        if (error) {
          console.error('Error fetching tokens:', error);
          return;
        }

        const tokens = tokensData?.map(t => t.token).filter(Boolean) || [];

        if (tokens.length > 0) {
          const message = {
            notification: {
              title: 'New Song Added!',
              body: `${payload.new.title} by ${payload.new.artist}`,
            },
            tokens: tokens,
          };

          try {
            const response = await admin.messaging().sendMulticast(message);
            console.log('Notifications sent:', response.successCount);
          } catch (err) {
            console.error('Error sending notifications:', err);
          }
        }
      }
    )
    .subscribe();

  console.log('Listening for new songs...');
})();
