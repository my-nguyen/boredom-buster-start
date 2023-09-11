package eu.maxkim.boredombuster.activity.fake.usecase

import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.activity.usecase.GetRandomActivity
import eu.maxkim.boredombuster.model.Result

class FakeGetRandomActivity(val isSuccessful: Boolean = true) : GetRandomActivity {
    override suspend fun invoke(): Result<Activity> {
        return if (isSuccessful) {
            Result.Success(activity1)
        } else {
            Result.Error(RuntimeException("Boom..."))
        }
    }
}
