# PolicyKitManager
Program:  PolicyKitManager

Creator:  Ameer Reza Jalal

Date:     Started on August 19, 2016

Info:     This application will manage PolicyKit policies for the end user. gksudo is no longer
          included with Ubuntu, with the common argument being to use pkexec instead. However,
          using GUI applications with pkexec can be time-consuming if one has to write policies
          for them manually. Additonally, this tempts novice users to just use sudo, which can be a
          security concern. For security reasons, this app only manages policies it creates.
          This program will require an installation of Java to run.

#Install Notes
Out of the Box:

To run:   To simply run, you may run 

          sudo PolicyKitManager.jar add [gui or nogui] [path-to-program] 
          
gui/nogui refers to if the app has a graphical interface.

Install to run anywhere:

First, create a symlink in /usr/bin like this:

          sudo ln -s PolicyKitManager.jar /usr/bin/polkitman
         
Then, run normally from any directory:

          sudo polkitman add [gui or nogui] [path-to-program]

#Changelog

v0.1-alpha-------------------------------------------------------------------------

Currently can add programs to PolicyKit. Currently writes policy to

                    /usr/share/polkit-1/actions/org.policykitmanager.policykit.policy
                    
Planned Features-------------------------------------------------------------------

Will be able to list previously added policies, and remove them upon request.
For security reasons, this program will not edit policies added manually or by other means. 
