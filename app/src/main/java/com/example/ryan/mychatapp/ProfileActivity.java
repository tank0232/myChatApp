package com.example.ryan.mychatapp;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {

    private Button sendFriendRequest;
    private Button declineFriendRequest;
    private TextView ProfileName;
    private TextView ProfileStatus;
    private ImageView ProfileImage;
    private DatabaseReference UsersReference;

    private String CURRENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;
    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FriendsReference = FirebaseDatabase.getInstance().getReference()
                .child("Friends");
        FriendsReference.keepSynced(true);


        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FriendRequestReference.keepSynced(true);

         NotificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
         NotificationsReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
         sender_user_id = mAuth.getCurrentUser().getUid();

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
       receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();

      sendFriendRequest = (Button)findViewById(R.id.profile_visit_send_req_btn);
      declineFriendRequest = (Button)findViewById(R.id.profile_decline_friend_req_btn);
      ProfileName = (TextView) findViewById(R.id.profile_visit_username);
      ProfileStatus = (TextView) findViewById(R.id.profile_visit_user_status);
      ProfileImage = (ImageView)findViewById(R.id.profile_visit_user_image);

      CURRENT_STATE = "not_friends";


      UsersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              String name = dataSnapshot.child("user_name").getValue().toString();
              String status = dataSnapshot.child("user_status").getValue().toString();
              String image = dataSnapshot.child("user_image").getValue().toString();
              ProfileName.setText(name);
              ProfileStatus.setText(status);
              Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(ProfileImage);

              FriendRequestReference.child(sender_user_id)
                      .addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                             if(dataSnapshot.exists())
                             {
                                 if (dataSnapshot.hasChild(receiver_user_id))
                                 {
                                     String req_type = dataSnapshot.child(receiver_user_id)
                                             .child("request_type").getValue().toString();
                                     if(req_type.equals("sent"))
                                     {
                                         CURRENT_STATE = "request_sent";
                                         sendFriendRequest.setText("Cancel Friend Request");

                                         declineFriendRequest.setVisibility(View.INVISIBLE);
                                         declineFriendRequest.setEnabled(false);



                                     }
                                     else if(req_type.equals("received"))
                                     {
                                         CURRENT_STATE = "request_received";
                                         sendFriendRequest.setText("Accept Friend Request");
                                         declineFriendRequest.setVisibility(View.VISIBLE);
                                         declineFriendRequest.setEnabled(true);
                                         
                                         declineFriendRequest.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 DeclineFriendRequest();
                                             }
                                         });

                                     }

                                 }

                             }
                             else
                             {
                                 FriendsReference.child(sender_user_id)
                                         .addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(DataSnapshot dataSnapshot) {
                                                 if(dataSnapshot.hasChild(receiver_user_id))
                                                 {
                                                     CURRENT_STATE = "friends";
                                                     sendFriendRequest.setText("Unfriend This Person");

                                                     declineFriendRequest.setVisibility(View.INVISIBLE);
                                                     declineFriendRequest.setEnabled(false);

                                                 }
                                             }

                                             @Override
                                             public void onCancelled(DatabaseError databaseError) {

                                             }
                                         });
                             }
                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {

                          }
                      });
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

       declineFriendRequest.setVisibility(View.INVISIBLE);
       declineFriendRequest.setEnabled(false);



      if(!sender_user_id.equals(receiver_user_id))
      {
          sendFriendRequest.setOnClickListener(new View.OnClickListener() {
              @RequiresApi(api = Build.VERSION_CODES.N)
              @Override
              public void onClick(View v) {
                  sendFriendRequest.setEnabled(false);

                  if(CURRENT_STATE.equals("not_friends"))
                  {
                      SendFriendRequestToaFriend();
                  }

                  if(CURRENT_STATE.equals("request_sent")){
                      CancelFriendRequest();
                  }

                  if(CURRENT_STATE.equals("request_received"))
                  {
                      AcceptFriendRequest();
                  }
                  if(CURRENT_STATE.equals("friends"))
                  {
                      UnFriendsFriend();
                  }
              }
          });
      }
      else
      {
          declineFriendRequest.setVisibility(View.INVISIBLE);
          sendFriendRequest.setVisibility(View.INVISIBLE);
      }


    }

    private void DeclineFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                sendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "not_friend";
                                sendFriendRequest.setText("Send Friend Request");

                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                declineFriendRequest.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void UnFriendsFriend() {
        FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                          FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful())
                                          {
                                              sendFriendRequest.setEnabled(true);
                                              CURRENT_STATE = "not_friends";
                                              sendFriendRequest.setText("Send Friend Request");

                                              declineFriendRequest.setVisibility(View.INVISIBLE);
                                              declineFriendRequest.setEnabled(false);
                                          }
                                      }
                                  });
                      }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void AcceptFriendRequest() {
        Calendar calFordAte = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calFordAte.getTime());

        FriendsReference.child(sender_user_id)
                .child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FriendsReference.child(receiver_user_id)
                                .child(sender_user_id)
                                .child("date")
                                .setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                sendFriendRequest.setEnabled(true);
                                                                CURRENT_STATE = "friends";
                                                                sendFriendRequest.setText("Unfriend this person");

                                                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                                                declineFriendRequest.setEnabled(false);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful())
                   {
                       FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                               .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful())
                              {
                                  sendFriendRequest.setEnabled(true);
                                  CURRENT_STATE = "not_friend";
                                  sendFriendRequest.setText("Send Friend Request");

                                  declineFriendRequest.setVisibility(View.INVISIBLE);
                                  declineFriendRequest.setEnabled(false);
                              }
                           }
                       });
                   }
            }
        });
    }

    private void SendFriendRequestToaFriend() {
         FriendRequestReference.child(sender_user_id)
                 .child(receiver_user_id).child("request_type")
                 .setValue("sent")
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful())
                         {
                             FriendRequestReference.child(receiver_user_id)
                                     .child(sender_user_id).child("request_type").setValue("received")
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful())
                                             {
                                                 HashMap<String, String> notificationData = new HashMap<String,String>();
                                                 notificationData.put("from",sender_user_id);
                                                 notificationData.put("type","request");

                                                 NotificationsReference.child(receiver_user_id).push().setValue(notificationData)
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {

                                                                 if(task.isSuccessful()) {
                                                                     sendFriendRequest.setEnabled(true);
                                                                     CURRENT_STATE = "request_sent";
                                                                     sendFriendRequest.setText("Cancel Friend Request");

                                                                     declineFriendRequest.setVisibility(View.INVISIBLE);
                                                                     declineFriendRequest.setEnabled(false);
                                                                 }
                                                             }
                                                         });



                                             }
                                         }
                                     });
                         }
                     }
                 });
    }
}
