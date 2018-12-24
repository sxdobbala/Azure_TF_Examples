package com.optum.pipeline

import com.optum.config.Config
import com.optum.utils.Helpers

def customExecutionLogic(Object commands,Object creds)
{
    def credentials = new Credentials()
    if (commands != null)
    {
        Map<String, String> namepass = new HashMap<String, String>()

        if(creds != null)
        {
            namepass = credentials.getCredentials(creds)

            for (i = 0; i< namepass.size(); i++)
            {
                x = namepass.keySet().toArray()[i]
                env."$x" = namepass.get(x)
            }
        }

        for (i=0; i< commands.size(); i++)
        {
            sh commands[i]
        }
    }
}