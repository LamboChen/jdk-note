package org.omg.CosNaming.NamingContextPackage;


/**
* org/omg/CosNaming/NamingContextPackage/NotEmpty.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-owindows-i586-cygwin/jdk8u40/2855/corba/src/share/classes/org/omg/CosNaming/nameservice.idl
* Tuesday, February 10, 2015 10:07:59 PM PST
*/

public final class NotEmpty extends org.omg.CORBA.UserException
{

  public NotEmpty ()
  {
    super(NotEmptyHelper.id());
  } // ctor


  public NotEmpty (String $reason)
  {
    super(NotEmptyHelper.id() + "  " + $reason);
  } // ctor

} // class NotEmpty
