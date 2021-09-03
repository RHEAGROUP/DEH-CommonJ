/*
 * HubController.java
 *
 * Copyright (c) 2020-2021 RHEA System S.A.
 *
 * Author: Sam Gerené, Alex Vorobiev, Nathanael Smiechowski 
 *
 * This file is part of DEH-CommonJ
 *
 * The DEH-CommonJ is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * The DEH-CommonJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package HubController;

import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import Reactive.ObservableValue;
import cdp4common.engineeringmodeldata.EngineeringModel;
import cdp4common.engineeringmodeldata.Iteration;
import cdp4common.sitedirectorydata.DomainOfExpertise;
import cdp4common.sitedirectorydata.EngineeringModelSetup;
import cdp4common.sitedirectorydata.IterationSetup;
import cdp4common.sitedirectorydata.Participant;
import cdp4common.sitedirectorydata.Person;
import cdp4common.sitedirectorydata.SiteDirectory;
import cdp4common.types.ContainerList;
import cdp4dal.Session;
import cdp4dal.SessionImpl;
import cdp4dal.dal.Credentials;
import cdp4servicesdal.CdpServicesDal;
import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableAll;
import io.reactivex.subjects.PublishSubject;

/**
 * Definition of the {@link HubController} which is responsible to provides {@link Session} related functionalities
 */
public final class HubController implements IHubController 
{
    /**
     * The current class logger
     */
    private Logger logger = LogManager.getLogger();
    
    /**
     * Backing field for {@link IsSessionOpen}
     */ 
    private DomainOfExpertise currentDomainOfExpertise;
    
    /**
     * Gets the current {@linkplain DomainOfExpertise}
     */
    @Override
    public DomainOfExpertise GetCurrentDomainOfExpertise()
    {
        return this.currentDomainOfExpertise;
    }

    /**
     * Backing field for {@link IsSessionOpen}
     */
    private Boolean isSessionOpen = false;
    
    /**
     * Gets a value indicating whether the session is open
     * 
     * @return a {@linkplain Boolean}
     */
    @Override
    public Boolean GetIsSessionOpen()
    {
        return this.isSessionOpen;
    }
    
    /**
     * Backing field for {@linkplain IsSessionOpenObservable()}
     */
    private ObservableValue<Boolean> isSessionOpenObservable = new ObservableValue<Boolean>();
    
    /**
     * Gets the {@linkplain Observable} from {@linkplain isSessionOpen} boolean field
     * 
     * @return an {@linkplain Observable} wrapping a value indicating whether the {@linkplain Session} is open
     */
    @Override
    public Observable<Boolean> GetIsSessionOpenObservable()
    {
        return this.isSessionOpenObservable.Observable();
    }
    
    /**
     * Sets the {@linkplain isSessionOpen} and call OnNext on {@linkplain isSessionOpenObservable}
     * 
     * @param value the {@linkplain Boolean} new value
     */
    private void SetIsSessionOpen(Boolean value)
    {
        this.isSessionOpen = value;
        this.isSessionOpenObservable.Value(value);
    }
    
    /**
     * Sets the open {@link Iteration}
     */
    private Iteration openIteration;
    
    /**
     * Gets the open {@link Iteration}
     * 
     * @return an {@linkplain Iteration}
     */
    @Override
    public Iteration GetOpenIteration()
    {
        return this.openIteration;
    }
            
    /**
     * The {@link Session} that is used to communicate with the data-store
     */
    private Session session;
    
    /**
     * Opens the {@linkplain Session}
     * 
     * @param credentials the {@link Credentials}
     * @return A {@link Boolean} indicating whether opening the session succeeded
     */
    @Override
    public Boolean Open(Credentials credentials)
    {
        this.session = new SessionImpl(new CdpServicesDal(), credentials); 

        this.session.open().join();
                
        return this.session.getAssembler().retrieveSiteDirectory() != null;
    }    

    /**
     * Gets the {@link EngineeringModelSetup}s contained in the site directory
     *  
     * @return A {@link ContainerList} of {@link EngineeringModelSetup}
     */
    @Override
    public ContainerList<EngineeringModelSetup> GetEngineeringModels()
    {
        SiteDirectory siteDirectory = this.GetSiteDirectory();
        
        if (siteDirectory != null)
        {
            return siteDirectory.getModel();
        }
        
        return null;
    }

    /**
     * Retrieves the <{@link SiteDirectory} that is loaded in the <{@link Session}
     * 
     * @return the {@link SiteDirectory}
     */
    @Override
    public SiteDirectory GetSiteDirectory()
    {
        if (this.session != null)
        {
            return this.session.retrieveSiteDirectory();            
        }
        
        return null;
    }
    
    /**
     * Gets the data source URI as string of the current {@linkplain Session}
     * 
     * @return a string representation of the URI or a empty string
     */
    @Override
    public String GetDataSourceUri()
    {
        if (this.session != null)
        {
            return this.session.getDataSourceUri();
        }
        
        return "";
    }
    
    /**
     * Gets the active person
     * 
     * @return The active {@link Person}
     */
    @Override
    public Person GetActivePerson()
    {
        if (this.session != null)
        {
            return this.session.getActivePerson();            
        }
        
        return null;
    }    
    
    /**
     * Reads an {@link Iteration} and set the active {@link DomainOfExpertise} for the {@link Iteration}
     * 
     * @param The {@link Iteration} to read
     * @param The {@link Domain} that reads the {@link Iteration}
     */
    @Override
    public void GetIteration(Iteration iteration, DomainOfExpertise domain)
    {
        this.session.read(iteration, domain).join();
        ImmutableMap<Iteration, Pair<DomainOfExpertise, Participant>> iterationDomainAndParticipant = this.GetIteration();
        this.openIteration = iterationDomainAndParticipant.keySet().stream().findFirst().get();
        this.currentDomainOfExpertise = iterationDomainAndParticipant.entrySet().stream().findFirst().get().getValue().getKey();
        
        this.SetIsSessionOpen(this.openIteration != null);
    }

    /**
     * Reads an {@link Iteration} and set the active @link DomainOfExpertise for the Iteration
     * 
     * @return a {@link ImmutableMap} of <{@link Iteration}, {@link Pair} <{@link DomainOfExpertise}, {@link Participant} >>
     */
    @Override
    public ImmutableMap<Iteration, Pair<DomainOfExpertise, Participant>> GetIteration()
    {
        return this.session.getOpenIterations();
    }

    /**
     * Closes the connection to the data-source
     */
    @Override
    public void Close()
    {
        if (!this.isSessionOpen)
        {
            this.logger.info("At first a connection should be opened.");
            return;
        }

        try
        {
            this.session.close().join();
            this.session = null;
            this.SetIsSessionOpen(false);
            this.currentDomainOfExpertise = null;
            this.openIteration = null;
        }
        catch (Exception exception)
        {
            this.logger.error(MessageFormat.format("During close operation an error has occured: {0}", exception));
        }
    }
    
    /**
     * Reloads the {@link Session}
     * 
     * @return A value indicating whether the {@link future} completed with success
     */
    @Override
    public Boolean Reload()
    {
        return this.RefreshOrReload(this.session.reload());
    }

    /**
     * Reloads the {@link Session}
     * 
     * @return A value indicating whether the {@link future} completed with success
     */
    @Override
    public Boolean Refresh()
    {
        return this.RefreshOrReload(this.session.refresh());
    }
    
    /**
     * Calls the {@link future} wrapped in try block and return a {@link Boolean}
     * 
     * @param future a {@link CompletableFuture}
     * @return A value indicating whether the {@link future} completed with success
     */
    private Boolean RefreshOrReload(CompletableFuture<Void> future)
    {
        try
        {
            future.get();
            return true;
        } 
        catch (InterruptedException interruptedException)
        {
            this.logger.catching(interruptedException);
        } 
        catch (ExecutionException executionException)
        {
            this.logger.catching(executionException);
        }
        
        return false;
    }

    /**
     * Loads an {@link Iteration} with the selected {@link DomainOfExpertise}
     * 
     * @param engineeringModelSetup
     * @param iterationSetup
     * @param domainOfExpertise
     * @return A value indicating whether the operation went well
     */
    @Override
    public boolean OpenIteration(EngineeringModelSetup engineeringModelSetup, IterationSetup iterationSetup, DomainOfExpertise domainOfExpertise)
    {
        try
        {
            EngineeringModel model = new EngineeringModel(engineeringModelSetup.getEngineeringModelIid(), 
                    this.session.getAssembler().getCache(), this.session.getCredentials().getUri());
            
            model.setEngineeringModelSetup(engineeringModelSetup);

            Iteration iteration = new Iteration(iterationSetup.getIterationIid(),
                    this.session.getAssembler().getCache(), this.session.getCredentials().getUri());

            model.getIteration().add(iteration);
            
            this.GetIteration(iteration, domainOfExpertise);
            return true;
        }
        catch (Exception exception)
        {
            this.logger.catching(exception);
            return false;
        }        
    }
}
