package eu.maxkim.boredombuster.activity.framework.datasource

import eu.maxkim.boredombuster.activity.model.Activity

// TestAndroidModels.kt
val androidActivity1 = Activity(
    name = "Learn to dance",
    type = Activity.Type.Recreational,
    participantCount = 2,
    price = 0.1f,
    accessibility = 0.2f,
    key = "112233",
    link = "www.dance.com"
)

val androidActivity2 = Activity(
    name = "Pet a dog",
    type = Activity.Type.Relaxation,
    participantCount = 1,
    price = 0.0f,
    accessibility = 0.1f,
    key = "223344",
    link = "www.dog.com"
)
